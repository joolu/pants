/**
 * Copyright (C) 2012 Typesafe, Inc. <http://www.typesafe.com>
 */

package org.pantsbuild.zinc.compiler

import com.martiansoftware.nailgun.NGContext
import java.io.File
import java.nio.file.Paths
import sbt.internal.inc.IncrementalCompilerImpl
import sbt.internal.util.{ BasicLogger, ConsoleLogger, ConsoleOut, StackTrace }
import sbt.util.{ ControlEvent, Level, LogEvent, Logger }
import xsbti.CompileFailed

import org.pantsbuild.zinc.analysis.AnalysisMap
import org.pantsbuild.zinc.options.Parsed
import org.pantsbuild.zinc.util.Util

// The normal sbt logger takes 5 seconds to start up in a native image. This is intended to be
// equivalent, while allowing the native image to run immediately.
object BareBonesLogger extends BasicLogger {
  import scala.Console.{ CYAN, GREEN, RED, YELLOW, RESET }

  val out = System.err

  override def trace(t: => Throwable): Unit = out.println(StackTrace.trimmed(t, getTrace))

  override def success(message: => String): Unit = {
    val colored = s"$GREEN[SUCCESS] $message$RESET"
    out.println(colored)
  }

  override def log(
    level: Level.Value,
    message: => String
  ): Unit = {
    val (colorStart, prefix) = level match {
      case Level.Debug => (CYAN, "[DEBUG]")
      case Level.Info => (GREEN, "[INFO]")
      case Level.Warn => (YELLOW, "[WARN]")
      case Level.Error => (RED, "[ERROR]")
    }
    val colored = s"$colorStart$prefix $message$RESET"
    out.println(colored)
  }

  override def logAll(events: Seq[LogEvent]): Unit = events.foreach(log)

  override def control(event: ControlEvent.Value, message: => String): Unit = {
    val colored = s"$GREEN[CONTROL: $event] $message$RESET"
    out.println(colored)
  }
}

/**
 * Command-line main class.
 */
object Main {
  val Command = "zinc-compiler"
  val Description = "scala incremental compiler"

  /**
   * Full zinc version info.
   */
  case class Version(published: String, timestamp: String, commit: String)

  /**
   * Get the zinc version from a generated properties file.
   */
  lazy val zincVersion: Version = {
    val props = Util.propertiesFromResource("zinc.version.properties", getClass.getClassLoader)
    Version(
      props.getProperty("version", "unknown"),
      props.getProperty("timestamp", ""),
      props.getProperty("commit", "")
    )
  }

  /**
   * For snapshots the zinc version includes timestamp and commit.
   */
  lazy val versionString: String = {
    import zincVersion._
    if (published.endsWith("-SNAPSHOT")) "%s %s-%s" format (published, timestamp, commit take 10)
    else published
  }

  /**
   * Print the zinc version to standard out.
   */
  def printVersion(): Unit = println("%s (%s) %s" format (Command, Description, versionString))

  /**
   * Run a compile.
   */
  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis

    val Parsed(settings, residual, errors) = Settings.parse(args)

    mainImpl(settings.withAbsolutePaths(Paths.get(".").toAbsolutePath.toFile), errors, startTime)
  }

  def nailMain(context: NGContext): Unit = {
    val startTime = System.currentTimeMillis

    val Parsed(settings, residual, errors) = Settings.parse(context.getArgs)

    mainImpl(settings.withAbsolutePaths(new File(context.getWorkingDirectory)), errors, startTime)
  }

  def mainImpl(settings: Settings, errors: Seq[String], startTime: Long): Unit = {
    val log = BareBonesLogger
    val isDebug = settings.consoleLog.logLevel <= Level.Debug

    // bail out on any command-line option errors
    if (errors.nonEmpty) {
      for (error <- errors) log.error(error)
      log.error("See %s -help for information about options" format Command)
      sys.exit(1)
    }

    if (settings.version) printVersion()

    if (settings.help) Settings.printUsage(Command, residualArgs = "<sources>")

    // if there are no sources provided, print outputs based on current analysis if requested,
    // else print version and usage by default
    if (settings.sources.isEmpty) {
      if (!settings.version && !settings.help) {
        printVersion()
        Settings.printUsage(Command)
        sys.exit(1)
      }
      sys.exit(0)
    }

    // Load the existing analysis for the destination, if any.
    val analysisMap = AnalysisMap.create(settings.analysis)
    val (targetAnalysisStore, previousResult) =
      InputUtils.loadDestinationAnalysis(settings, analysisMap, log)
    val inputs = InputUtils.create(settings, analysisMap, previousResult, log)

    if (isDebug) {
      log.debug(s"Inputs: $inputs")
    }

    try {
      // Run the compile.
      val result = new IncrementalCompilerImpl().compile(inputs, log)

      // Store the output if the result changed.
      if (result.hasModified) {
        targetAnalysisStore.set(
          // TODO
          sbt.internal.inc.ConcreteAnalysisContents(result.analysis, result.setup)
        )
      }

      log.info("Compile success " + Util.timing(startTime))

      // if compile is successful, jar the contents of classesDirectory and copy to outputJar
      if (settings.outputJar.isDefined) {
        val outputJarPath = settings.outputJar.get.toPath
        val classesDirectory = settings.classesDirectory
        log.debug("Creating JAR at %s, for files at %s" format (outputJarPath, classesDirectory))
        OutputUtils.createClassesJar(classesDirectory, outputJarPath, settings.creationTime)
      }
    } catch {
      case e: CompileFailed =>
        log.error("Compile failed " + Util.timing(startTime))
        sys.exit(1)
      case e: Exception =>
        if (isDebug) e.printStackTrace
        val message = e.getMessage
        if (message ne null) log.error(message)
        sys.exit(1)
    }
  }
}

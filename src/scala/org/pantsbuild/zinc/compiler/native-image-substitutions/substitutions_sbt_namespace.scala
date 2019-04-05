package sbt.util

import org.apache.logging.log4j.core._

final class Target_sbt_util_LogExchange {
  private[sbt] def init(): LoggerContext = {
    import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
    import org.apache.logging.log4j.core.config.Configurator
    val builder = ConfigurationBuilderFactory.newConfigurationBuilder
    builder.setConfigurationName("sbt.util.logging")
    val ctx = Configurator.initialize(builder.build())
    ctx match { case x: LoggerContext => x }
  }
}

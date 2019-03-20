# Copyright 2017 Pants project contributors (see CONTRIBUTORS.md).
# Licensed under the Apache License, Version 2.0 (see LICENSE).

# TODO: why don't //:scalac, //:scala-library, and //:scala-reflect work?
scalac_rev = '2.12.8'
jar_library(
  name='scala-path',
  jars=[
    jar('org.scala-lang', 'scala-compiler', scalac_rev),
    jar('org.scala-lang', 'scala-library', scalac_rev),
    jar('org.scala-lang', 'scala-reflect', scalac_rev),
  ],
)

scala_library(
  provides=scala_artifact(
    org='org.pantsbuild',
    name='zinc-compiler',
    repo=public,
    publication_metadata=pants_library('The SBT incremental compiler for nailgun')
  ),
  dependencies=[
    ':scala-path',
    '3rdparty/jvm/com/martiansoftware:nailgun-server',
    '3rdparty/jvm/org/scala-lang/modules:scala-java8-compat',
    '3rdparty/jvm/org/scala-sbt:io',
    '3rdparty/jvm/org/scala-sbt:sbt-compiler-bridge',
    '3rdparty/jvm/org/scala-sbt:util-logging',
    '3rdparty/jvm/org/scala-sbt:zinc',
    '3rdparty:guava',
    '3rdparty:jsr305',
    'src/scala/org/pantsbuild/zinc/analysis',
    'src/scala/org/pantsbuild/zinc/cache',
    'src/scala/org/pantsbuild/zinc/options',
    'src/scala/org/pantsbuild/zinc/scalautil',
    'src/scala/org/pantsbuild/zinc/util',
    'src/scala/META-INF/native-image/org/pantsbuild/zinc/compiler:zinc-native-image-configuration',
    'src/scala/org/pantsbuild/zinc/compiler/native-image-substitutions',
    'src/scala/org/pantsbuild/zinc/compiler/native-image-substitutions:statics',
  ],
  strict_deps=True,
  platform='java8',
)

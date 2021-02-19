ThisBuild / name := "$name$"
ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "$version$"
ThisBuild / organization := "$organization$"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.mavenLocal

// PRIVATE RESOLVERS
resolvers ++= Seq(
  ("Artifactory Releases" at "http://artifactory.service.iad1.consul:8081/artifactory/libs-release/").withAllowInsecureProtocol(true),
  ("Artifactory Snapshots" at "http://artifactory.service.iad1.consul:8081/artifactory/libs-snapshot/").withAllowInsecureProtocol(true)
)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full)
enablePlugins(JavaAppPackaging)

configs(IntegrationTest)
Defaults.itSettings

val Http4sVersion = "0.21.4"

libraryDependencies ++= Seq(
  // ff (PRIVATE REPO)
  "com.tremorvideo" %% "lib-feature-flags" % "3.1.0-SNAPSHOT", // PUBLISHED LOCALLY
  "com.tremorvideo" %% "lib-api" % "0.29.2",

  // override
  "org.json4s" %% "json4s-native" % "3.6.10",
  "io.monix" %% "monix" % "3.3.0",

  // config
  "com.github.pureconfig" %% "pureconfig" % "0.14.0",

  // metrics
  "com.tremorvideo" %% "lib-metrics" % "0.2.0",

  // date/time
  "joda-time" % "joda-time" % "2.10.9",

  // test
  "org.scalatest" %% "scalatest" % "3.2.3",
  "org.scalacheck" %% "scalacheck" % "1.15.2",

  // http
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,

  // grpc
  "io.grpc" % "grpc-all" % "1.35.0",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",

  // logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-core" % "1.2.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "slf4j-api" % "1.7.28"
)

PB.targets in Compile := Seq(
  scalapb.gen(
    flatPackage = true,
    grpc = true
  ) -> (sourceManaged in Compile).value,

  aksharp.Generator -> (sourceManaged in Compile).value // LOCALLY PUBLISHED
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-deprecation",
  "-unchecked",
  "-Xlint",
  "-feature",
  "-language:existentials",
  "-language:reflectiveCalls",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Yrangepos"
)
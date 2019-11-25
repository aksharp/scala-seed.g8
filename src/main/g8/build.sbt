ThisBuild / name := "$name$"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "$version$"
ThisBuild / organization := "$organization$"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.mavenLocal

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8",
  "org.scalacheck" %% "scalacheck" % "1.14.2",
  "io.grpc" % "grpc-services" % "1.25.0",
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

  aksharp.Generator -> (sourceManaged in Compile).value
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

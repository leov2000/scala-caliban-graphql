name := "scala-caliban-graphql"

version := "0.1"

scalaVersion := "2.13.8"

val akkaVersion = "2.6.20"
val akkaHttpVersion = "10.2.10"
val calibanVersion = "2.0.2"
val circeVersion = "1.2.10"
val zioVersion = "2.0.5"
val logbackVersion = "1.3.0"
val scalaLoggingVersion = "3.9.4"

libraryDependencies ++= Seq(
  "com.github.ghostdogpr" %% "caliban" % calibanVersion,
  "com.github.ghostdogpr" %% "caliban-akka-http" % calibanVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % circeVersion,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
)

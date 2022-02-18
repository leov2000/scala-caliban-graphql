name := "scala-caliban-graphql"

version := "0.1"

scalaVersion := "2.13.8"

val akkaVersion = "2.6.17"
val akkaHttpVersion = "10.2.8"
val calibanVersion = "1.3.3"
val circeVersion = "0.19.3"
val zioVersion = "1.0.13"
val logbackVersion = "1.2.10"
val scalaLoggingVersion = "3.9.4"

libraryDependencies ++= Seq(
  "com.github.ghostdogpr" %% "caliban" % calibanVersion,
  "com.github.ghostdogpr" %% "caliban-akka-http" % calibanVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % circeVersion,
  "dev.zio" %% "zio" % zioVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
)
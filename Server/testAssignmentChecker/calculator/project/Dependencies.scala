import sbt._

object Dependencies {
  val commonLibVersion = "0.4"
  val scalaLoggingVersion = "3.9.2"
  val logbackVersion = "1.2.3"
  val catsVersion = "2.0.0"

  val commonLib = "com.eg.assignment.common" %% "common-lib" % commonLibVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val cats = "org.typelevel" %% "cats-core" % catsVersion
}

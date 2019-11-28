import sbt._

object Dependencies {
  val scalaVersion = "2.12.10"
  val classUtilVersion = "1.5.1"
  val circeVersion = "0.12.3"

  val classUtil = "org.clapper" %% "classutil" % classUtilVersion
  val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaVersion
  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
}

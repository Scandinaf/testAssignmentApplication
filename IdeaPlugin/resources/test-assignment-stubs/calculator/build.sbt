

scalaVersion in Global := "2.12.10"
version in Global := "0.1-SNAPSHOT"
unmanagedBase := baseDirectory.value / "assignment"

lazy val root = (project in file(".")).settings(
  name := "$projectName",
  scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked"),
  assemblyJarName in assembly := "assignment.jar",
  assemblyOutputPath in assembly := file("target/" + (assemblyJarName in assembly).value),
  test in assembly := {}
)
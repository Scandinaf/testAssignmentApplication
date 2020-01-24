scalaVersion in Global := "2.12.10"
version in Global := "0.1-SNAPSHOT"
unmanagedBase := baseDirectory.value / "assignment"

lazy val root = (project in file(".")).settings(
  addCompilerPlugin(scalafixSemanticdb),
  libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0",
  scalafixDependencies in ThisBuild += "com.github.vovapolu" %% "scaluzzi" % "0.1.3",
  name := "shortestpath",
  scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-Xlint", "-Ywarn-unused", "-Xfatal-warnings", "-Yrangepos"),
  assemblyJarName in assembly := "assignment.jar",
  assemblyOutputPath in assembly := file("target/" + (assemblyJarName in assembly).value),
  test in assembly := {},
)

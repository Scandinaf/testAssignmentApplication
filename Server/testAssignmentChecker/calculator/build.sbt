import Dependencies._

name := "calculator_assignment_checker"
version := "0.1"
scalaVersion in Scope.Global := "2.12.10"
unmanagedBase := baseDirectory.value / "assignment"

lazy val root = Project("calculator_assignment_checker", file("."))
  .settings(
    mainClass in(Compile, run) := Some("com.eg.assignment.checker.Main"),
    mainClass in assembly := Some("com.eg.assignment.checker.Main"),
    scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-Ypartial-unification"),
    assemblyOutputPath in assembly := baseDirectory.value /
      "target" / (name.value + "-" + version.value + ".jar"),
    libraryDependencies ++= Seq(commonLib, scalaLogging, logback),
    test in assembly := {},

    assemblyMergeStrategy in assembly := {
      case PathList(ps @ _*) if ps.last endsWith "module-info.class" => MergeStrategy.first
      case x                                                         =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },

    imageNames in docker := Seq(
      ImageName(s"${ name.value }:latest")
    ),

    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${ artifact.name }"
      val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))

      new Dockerfile {
        from("adoptopenjdk/openjdk12:latest")
        add(artifact, artifactTargetPath)
        entryPointShell("java", "-cp", s"$artifactTargetPath:$$ASSIGNMENT_JAR_PATH", mainclass)
      }
    }
  ).enablePlugins(DockerPlugin)
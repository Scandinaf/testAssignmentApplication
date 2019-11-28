name := "common-lib"
version := "0.4"
organization := "com.eg.assignment.common"
scalaVersion := Dependencies.scalaVersion


lazy val root = Project("common-lib", file("."))
  .settings(
    scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked"),
    assemblyOutputPath in assembly := baseDirectory.value /
      "target" / (name.value + "-" + version.value + ".jar"),
    libraryDependencies ++= Dependencies.circe ++
      Seq(Dependencies.classUtil, Dependencies.scalaReflect),
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList(ps @ _*) if ps.last endsWith "module-info.class" => MergeStrategy.first
      case x                                                         =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
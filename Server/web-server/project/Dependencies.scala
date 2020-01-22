import sbt._

object Dependencies {
  val activationVersion = "1.1.1"
  val akkaHttpVersion = "10.1.11"
  val akkaHttpCirceVersion = "1.30.0"
  val akkaStreamVersion = "2.5.26"
  val dockerClientVersion = "0.9.9"
  val scalaLoggingVersion = "3.9.2"
  val logbackVersion = "1.2.3"
  val commonLibVersion = "0.4"
  val catsVersion = "2.0.0"
  val couchbaseScalaVersion = "1.0.0"

  val activation = "javax.activation" % "activation" % activationVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion
  val dockerClient = "com.whisk" %% "docker-testkit-impl-spotify" % dockerClientVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val commonLib = "com.eg.assignment.common" %% "common-lib" % commonLibVersion
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val couchbaseScala = "com.couchbase.client" %% "scala-client" % couchbaseScalaVersion
}

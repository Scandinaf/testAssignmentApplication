package com.eg.assignment.server.initialization

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.eg.assignment.common.helper.ENVVariableHelper
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

trait ServerInitialization extends LazyLogging {
  implicit val system = ActorSystem("web-server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val globalConfig: Config = ConfigFactory.load()
  val (assignmentDirPath: String, dockerHost: String) =
    (for {
      assignmentDirPath <- ENVVariableHelper.getENVVariable("ASSIGNMENT_DIRECTORY_PATH")
      dockerHost <- ENVVariableHelper.getENVVariable("DOCKER_HOST")
    } yield (assignmentDirPath, dockerHost))
      .fold(ex => {
        logger.error("An error occurred when attempting to initialize the application.", ex)
        terminateSystem
      }, identity(_))

  def terminateSystem: Unit = {
    system.terminate()
    System.exit(0)
  }
}

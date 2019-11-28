package com.eg.assignment.server.initialization

import akka.actor.ActorSystem
import cats.implicits._
import com.eg.assignment.server.helper.DockerHelper
import com.eg.assignment.server.service.docker.{BaseDockerAssignmentChecker, DockerAssignmentChecker}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

trait DockerAssignmentsInitialization extends ServerInitialization {
  implicit val system: ActorSystem
  implicit val executionContext: ExecutionContextExecutor
  val dockerAssignmentService: Map[String, BaseDockerAssignmentChecker] = Map(
    "calculator" -> new DockerAssignmentChecker(
      "calculator_assignment_checker",
      "result.json",
      Seq(
        5 seconds,
        2 second,
        2 second,
        1 second,
        5 seconds,
        2 second,
        2 second,
        1 second,
        5 seconds,
        2 second,
        2 second,
        1 second,
      )
    )
  )

  DockerHelper
    .dockerInteractionCommandWrapper(dockerManager => {
      dockerAssignmentService.values.toList.map(dockerChecker =>
        DockerHelper.isDockerImageExist(dockerChecker.imageName)(dockerManager)
      ).sequence
        .leftMap(ex => {
          logger.error("An error occurred when attempting to initialize the assignment checker.", ex)
          terminateSystem
        })
    })
}

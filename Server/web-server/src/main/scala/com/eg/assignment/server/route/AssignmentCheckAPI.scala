package com.eg.assignment.server.route

import java.time.ZonedDateTime

import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.server.{Directives, Route}
import cats.data.EitherT
import cats.implicits._
import com.eg.assignment.common.json.JsonFormats.assignmentCheckResultCodec
import com.eg.assignment.common.model.result.AssignmentCheckResult
import com.eg.assignment.server.Main.{dockerAssignmentService, executionContext}
import com.eg.assignment.server.exception.{DockerContainerStillRunningException, ParameterNotFoundException}
import com.eg.assignment.server.model.Assignment
import com.eg.assignment.server.route.converter.MultipartFormDataConverter.{MultipartFormDataCompanion, assignmentWithStreamConverter}
import com.eg.assignment.server.service.LeaderboardService
import com.typesafe.scalalogging.LazyLogging
import io.circe.ParsingFailure

import scala.util.{Failure, Success}

class AssignmentCheckAPI(
  leaderboardService: LeaderboardService
) extends Directives with RouteExceptionHandler with LazyLogging {
  def routes: Route =
    path("docker" / "assignment" / "check") {
      post {
        entity(as[Multipart.FormData]) { data =>
          val now = ZonedDateTime.now
          val resultF = for {
            assignment <- data.as[Assignment]
            entity <- dockerAssignmentService(assignment.projectName).run(assignment)
            _ <- EitherT {
              leaderboardService.saveAttempt(assignment.projectName, assignment.userInformation, now, entity)
            }.leftMap[Exception](new RuntimeException(_))
          } yield entity
          onComplete(resultF.value) {
            case Success(value) =>
              value.fold(
                {
                  case _: ParsingFailure                       => buildBadRequest(
                    "Failed to deserialize the transferred Json. Check the data you are sending."
                  )
                  case ex: ParameterNotFoundException          => buildBadRequest(ex.getMessage)
                  case _: DockerContainerStillRunningException =>
                    buildOK(AssignmentCheckResult.apply(
                      isPassed = false,
                      additionalInformation =
                        """Check your implementation, you may have an infinite loop
                          |or something that prevents the system from functioning properly.""".stripMargin.some
                    ))
                  case ex                                      =>
                    logger.error("An unforeseen situation has arisen within the framework of the annex.", ex)
                    buildInternalServerError(
                      "Something went wrong, contact me at the next address - 'sergeyqwertyborovskiy@gmail.com'."
                    )
                },
                result => buildOK(result)
              )
            case Failure(value) =>
              logger.error("An unforeseen situation has arisen within the framework of the annex.", value)
              buildInternalServerError(
                "Something went wrong, contact me at the next address - 'sergeyqwertyborovskiy@gmail.com'."
              )
          }
        }
      }
    }
}

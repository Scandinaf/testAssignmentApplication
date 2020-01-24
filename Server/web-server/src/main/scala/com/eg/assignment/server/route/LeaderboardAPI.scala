package com.eg.assignment.server.route

import akka.http.scaladsl.server.{Directives, Route}
import com.eg.assignment.common.model.result.{AssignmentCheckResult, TestResult, TestSuiteResult}
import com.eg.assignment.server.Main.executionContext
import com.eg.assignment.server.model.AssignmentAttempt
import com.eg.assignment.server.service.LeaderboardService
import com.typesafe.scalalogging.LazyLogging
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import LeaderboardAPI._
import com.eg.assignment.common.model.assignment.UserInformation

import scala.concurrent.{ExecutionContext, Future}

class LeaderboardAPI(
  leaderboardService: LeaderboardService
) extends Directives with RouteExceptionHandler with LazyLogging {
  import FailFastCirceSupport._

  val routes: Route =
    path("api" / "leaderboard") {
      get {
        parameters(
          'projectName.as[String],
          'from.as[Long].?,
          'to.as[Long].?,
          'limit.as[Int].?,
          'offset.as[Int].?
        ) { (projectName, from, to, limit, offset) =>
          complete {
            leaderboardService
              .fetchLeaders(projectName, from, to, limit, offset)
              .handleErrors
          }
        }
      }
    }
}

object LeaderboardAPI {
  // TODO: move to some utils
  implicit class FEHandler[T](val resp: Future[Either[String, T]]) extends AnyVal {
    def handleErrors(implicit enc: io.circe.Encoder[T], ec: ExecutionContext): Future[Json] = {
      resp.map(_.fold(Json.fromString, _.asJson))
    }
  }

  implicit val userInformationCodec: Codec[UserInformation] = deriveCodec[UserInformation]
  implicit val testResultCodec: Codec[TestResult] = deriveCodec[TestResult]
  implicit val testSuiteResultCodec: Codec[TestSuiteResult] = deriveCodec[TestSuiteResult]
  implicit val assignmentCheckResultCodec: Codec[AssignmentCheckResult] = deriveCodec[AssignmentCheckResult]
  implicit val assignmentAttemptCodec: Codec[AssignmentAttempt] = deriveCodec[AssignmentAttempt]
}
package com.eg.assignment.server.service

import java.time.ZonedDateTime
import java.util.UUID
import java.{util => ju}

import cats.data.EitherT
import cats.instances.future._
import cats.syntax.either._
import com.eg.assignment.server.dao.LeaderboardDao
import com.eg.assignment.server.model.AssignmentAttempt
import LeaderboardService.ServiceError
import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.assignment.common.model.result.AssignmentCheckResult

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class LeaderboardService(
  dao: LeaderboardDao
)(implicit ec: ExecutionContext) {

  def fetchLeaders(
    projectName: String,
    fromEpochSec: Option[Long],
    toEpochSec: Option[Long],
    limit: Option[Int],
    offset: Option[Int]): Future[Either[ServiceError, Seq[AssignmentAttempt]]] = {
      EitherT(dao.fetchLeaders(projectName, fromEpochSec, toEpochSec, limit, offset))
        .leftMap(identity)
        .value
    }

  def saveAttempt(
                 projectName: String,
                 user: UserInformation,
                 dateTime: ZonedDateTime,
                 result: AssignmentCheckResult,
                 ): Future[Either[ServiceError, AssignmentAttempt]] = {
    val id = UUID.randomUUID()
    val assignmentAttempt = AssignmentAttempt(
      id = id,
      projectName = projectName,
      user = user,
      dateTime = dateTime,
      result = result,
    )
    dao.saveAttempt(assignmentAttempt)
  }

  def fetchAllProjectNames: Future[Either[ServiceError, Seq[String]]] = {
    dao.fetchAllProjectNames
  }

}

object LeaderboardService {
  // TODO: extract to a more generic object
  // TODO: make sealed trait
  type ServiceError = String
}
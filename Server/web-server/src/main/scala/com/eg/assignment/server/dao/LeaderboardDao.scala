package com.eg.assignment.server.dao

import com.eg.assignment.server.model.AssignmentAttempt
import LeaderboardDao.DaoError

import scala.concurrent.Future

trait LeaderboardDao {
  def fetchLeaders(
    projectName: String,
    fromEpochSec: Option[Long],
    toEpochSec: Option[Long],
    limit: Option[Int],
    offset: Option[Int]
  ): Future[Either[DaoError, Seq[AssignmentAttempt]]]

  def saveAttempt(assignmentAttempt: AssignmentAttempt): Future[Either[DaoError, AssignmentAttempt]]

  def fetchAllProjectNames: Future[Either[DaoError, Seq[String]]]
}

object LeaderboardDao {
  // TODO: extract to a more generic object
  // TODO: make sealed trait
  type DaoError = String
}
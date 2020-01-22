package com.eg.assignment.server.dao.cb

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.syntax.either._
import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, Cluster}
import com.couchbase.client.scala.implicits.Codec
import com.couchbase.client.scala.query.{QueryOptions, QueryParameters}
import com.eg.assignment.server.dao.db.CouchbaseDao
import com.eg.assignment.server.dao.LeaderboardDao
import com.eg.assignment.server.dao.LeaderboardDao.DaoError
import com.eg.assignment.server.model.AssignmentAttempt
import com.eg.assignment.server.dao.model.DbAssignmentAttempt
import LeaderboardCouchbaseDao._

import scala.concurrent.{ExecutionContext, Future}

class LeaderboardCouchbaseDao private (
  override protected val cluster: AsyncCluster,
  override protected val bucket: AsyncBucket,
)(implicit ec: ExecutionContext) extends LeaderboardDao with CouchbaseDao {

  private val bucketName = bucket.name
  private val fetchStmt =
    s"""select $bucketName.* from `$bucketName` where
       | projectName = $$projectName and
       | epochSeconds >= $$from and epochSeconds <= $$to
       | order by result.resultScore
       | limit $$limit offset $$offset;""".stripMargin
  private val fetchProjectsStmt =
    s"""select distinct raw $bucketName.projectName from `$bucketName` order by projectName;"""

  override def fetchLeaders(
    projectName: String,
    fromEpochSec: Option[Long],
    toEpochSec: Option[Long],
    limit: Option[Int],
    offset: Option[Int]
  ): Future[Either[DaoError, Seq[AssignmentAttempt]]] = {
    cluster
      .query(fetchStmt, QueryOptions().parameters(QueryParameters.Named(
        "projectName" -> projectName,
        "from" -> fromEpochSec.getOrElse(0L),
        "to" -> toEpochSec.getOrElse(Long.MaxValue),
        "limit" -> limit.getOrElse(Int.MaxValue),
        "offset" -> offset.getOrElse(0),
      )))
      .map(_.rowsAs[DbAssignmentAttempt](assignmentAttemptCodec)
        .toEither
        .map(_.map(_.asDomain))
        .leftMap(_.getMessage))
  }

  override def saveAttempt(
    assignmentAttempt: AssignmentAttempt
  ): Future[Either[DaoError, AssignmentAttempt]] = {
    bucket.defaultCollection
      .insert(assignmentAttempt.id.toString, assignmentAttempt.asDb)(assignmentAttemptCodec)
      .map(_ => assignmentAttempt.asRight)
  }

  override def fetchAllProjectNames: Future[Either[DaoError, Seq[String]]] = {
    cluster
      .query(fetchProjectsStmt)
      .map(_.rowsAs[String]
        .toEither
        .leftMap(_.getMessage))
  }
}

object LeaderboardCouchbaseDao {
  
  def create(
    ip: String,
    user: String,
    password: String,
    bucketName: String
  )(
    implicit ec: ExecutionContext
  ): Either[DaoError, LeaderboardCouchbaseDao] = {
    for {
      cluster <- Option {
        CouchbaseDao.clusters.computeIfAbsent(ip, _ => Cluster.connect(ip, user, password).getOrElse(null))
      }.toRight(s"Cannot connect to cluster at $ip")
      bucket <- Option(cluster.bucket(bucketName))
        .toRight(s"Cannot open bucket $bucketName")
    } yield new LeaderboardCouchbaseDao(cluster.async, bucket.async)
  }

  implicit val assignmentAttemptCodec: Codec[DbAssignmentAttempt] = Codec.codec[DbAssignmentAttempt]

  implicit class DbConverter(val entity: AssignmentAttempt) extends AnyVal {
    def asDb: DbAssignmentAttempt = DbAssignmentAttempt(
      id = entity.id,
      projectName = entity.projectName,
      user = entity.user,
      epochSeconds = entity.dateTime.toEpochSecond,
      result = entity.result,
    )
  }

  implicit class FromDbConverter(val entity: DbAssignmentAttempt) extends AnyVal {
    def asDomain: AssignmentAttempt = AssignmentAttempt(
      id = entity.id,
      projectName = entity.projectName,
      user = entity.user,
      dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(entity.epochSeconds), ZoneOffset.UTC),
      result = entity.result,
    )
  }
}
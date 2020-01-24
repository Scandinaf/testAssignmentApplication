package com.eg.assignment.server

import akka.http.scaladsl.server.{HttpApp, Route}
import com.eg.assignment.server.dao.cb.LeaderboardCouchbaseDao
import com.eg.assignment.server.initialization.DockerAssignmentsInitialization
import com.eg.assignment.server.model.ServerConfiguration
import com.eg.assignment.server.route.{AssignmentCheckAPI, FrontendAPI, LeaderboardAPI}
import com.eg.assignment.server.service.LeaderboardService

import scala.concurrent.Future

object Main extends App with DockerAssignmentsInitialization {
  val cbConfig = globalConfig.getConfig("db.couchbase")
  val cbClusterIp = cbConfig.getString("cluster.ip")
  val cbClusterUser = cbConfig.getString("cluster.user")
  val cbClusterPassword = cbConfig.getString("cluster.password")
  val cbBucketName = cbConfig.getString("bucket.name")
  LeaderboardCouchbaseDao
    .create(cbClusterIp, cbClusterUser, cbClusterPassword, cbBucketName)
    .fold(
      err => Future.successful(s"Failed to connect to couchbase: $err"),
      leaderboardDao => {
        val leaderboardService  = new LeaderboardService(leaderboardDao)
        val frontendAPI = new FrontendAPI(leaderboardService)
        val assignmentCheckAPI = new AssignmentCheckAPI(leaderboardService)
        val leaderboardAPI = new LeaderboardAPI(leaderboardService)
        val sc = ServerConfiguration.apply(globalConfig.getConfig("server"))
        val server: HttpApp = new HttpApp {
          override val routes: Route = concat(
            frontendAPI.routes,
            assignmentCheckAPI.routes,
            leaderboardAPI.routes,
          )
        }
        server.startServer(sc.host, sc.port)
      }
    )
}

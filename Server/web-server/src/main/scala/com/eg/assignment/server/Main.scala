package com.eg.assignment.server

import akka.http.scaladsl.Http
import com.eg.assignment.server.initialization.DockerAssignmentsInitialization
import com.eg.assignment.server.model.ServerConfiguration
import com.eg.assignment.server.route.AssignmentCheckAPI

import scala.io.StdIn

object Main extends App with DockerAssignmentsInitialization {
  private val sc = ServerConfiguration.apply(globalConfig.getConfig("server"))
  val bindingFuture = Http().bindAndHandle(AssignmentCheckAPI.apply, sc.host, sc.port)

  logger.info(s"Server online at http://${ sc.host }:${ sc.port }/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => terminateSystem)
}

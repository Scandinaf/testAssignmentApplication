package com.eg.assignment.server.helper

import akka.pattern.after
import cats.data.EitherT
import cats.implicits._
import com.eg.assignment.server.Main.{executionContext, system}
import com.eg.assignment.server.exception.{DockerContainerNotFoundException, DockerContainerStillRunningException, DockerImageNotFoundException}
import com.eg.assignment.server.model.DockerManager
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient.RemoveContainerParam
import com.typesafe.scalalogging.LazyLogging
import com.whisk.docker.impl.spotify.SpotifyDockerFactory
import com.whisk.docker.{DockerCommandExecutor, InspectContainerResult}

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}

object DockerHelper extends LazyLogging {

  def dockerInteractionCommandWrapper[L, R](
    f: DockerManager => EitherT[Future, L, R]
  ): EitherT[Future, L, R] = {
    val dockerClient: DefaultDockerClient = DefaultDockerClient.fromEnv().build()
    val dockerCommandExecutor: DockerCommandExecutor = new SpotifyDockerFactory(dockerClient).createExecutor()
    val result = f(DockerManager(dockerClient, dockerCommandExecutor))
    result.value.onComplete(_ => dockerCommandExecutor.close())
    result
  }

  def stopAndReleaseDockerContainer(
    containerId: String
  )(
    dockerManager: DockerManager
  ): Future[Unit] =
    Future.successful(
      dockerManager
        .dockerClient
        .removeContainer(
          containerId,
          RemoveContainerParam.forceKill(true),
          RemoveContainerParam.removeVolumes(true)
        ))

  def waitCompletionDockerContainer(
    containerId: String,
    delays: Seq[FiniteDuration],
  )(
    dockerManager: DockerManager
  ): EitherT[Future, Exception, InspectContainerResult] =
    if (delays.nonEmpty)
      waitCompletionDockerContainer(containerId, delays.head, delays.tail)(dockerManager)
    else
      waitCompletionDockerContainer(containerId, 0 seconds, Seq.empty)(dockerManager)


  private def waitCompletionDockerContainer(
    containerId: String,
    initDelay: FiniteDuration,
    delays: Seq[FiniteDuration],
  )(
    dockerManager: DockerManager
  ): EitherT[Future, Exception, InspectContainerResult] =
    EitherT.apply(after(initDelay, system.scheduler)(
      getDockerContainerState(containerId)(dockerManager)
        .flatMap[Exception, InspectContainerResult](state => {
          if (!state.running)
            EitherT.fromEither[Future](state.asRight[Exception])
          else if (!delays.nonEmpty)
            EitherT.fromEither[Future](
              new DockerContainerStillRunningException(s"ContainerId - $containerId, delays - $delays")
                .asLeft[InspectContainerResult]
            )
          else {
            val nextDelay = delays.head
            logger.info(
              s"""The container is still running and the following iteration will be performed.
                 |ContainerId - $containerId; Delay - $nextDelay.""".stripMargin
            )
            waitCompletionDockerContainer(containerId, nextDelay, delays.tail)(dockerManager)
          }
        }).value
    ))

  def getDockerContainerState(
    containerId: String
  )(
    dockerManager: DockerManager
  ): EitherT[Future, DockerContainerNotFoundException, InspectContainerResult] =
    EitherT.fromOptionF(
      dockerManager.dockerCommandExecutor.inspectContainer(containerId),
      new DockerContainerNotFoundException(s"Not found container - $containerId.")
    )

  def isDockerImageExist(
    imageName: String,
    tag: String = "latest"
  )(
    dockerManager: DockerManager
  ): EitherT[Future, DockerImageNotFoundException, Unit] = {
    val fullImageName = s"$imageName:$tag"
    EitherT.apply(dockerManager.dockerCommandExecutor
      .listImages
      .map(_.exists(_.equals(fullImageName))).map {
      case true  => ().asRight
      case false => new DockerImageNotFoundException(s"Not found docker image - $imageName.").asLeft
    })
  }
}

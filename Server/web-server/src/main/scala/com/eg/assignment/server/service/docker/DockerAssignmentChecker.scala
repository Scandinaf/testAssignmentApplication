package com.eg.assignment.server.service.docker

import java.nio.file.{Path, Paths}

import cats.data.EitherT
import cats.implicits._
import com.eg.assignment.common.helper.FileAssistant
import com.eg.assignment.common.json.JsonFormats.assignmentCheckResultCodec
import com.eg.assignment.common.model.result.AssignmentCheckResult
import com.eg.assignment.server.Main.executionContext
import com.eg.assignment.server.exception.InternalError
import com.eg.assignment.server.helper.{DockerHelper, StreamHelper}
import com.eg.assignment.server.model.Assignment
import com.eg.assignment.server.service.AssignmentFileSystemHelper
import com.typesafe.scalalogging.LazyLogging
import com.whisk.docker.{DockerCommandExecutor, DockerContainer, InspectContainerResult}
import io.circe.parser.decode

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration


class DockerAssignmentChecker(
  val imageName: String,
  resultFileName: String,
  delays: Seq[FiniteDuration],
)
  extends BaseDockerAssignmentChecker
    with LazyLogging
    with AssignmentFileSystemHelper
    with AssignmentDockerHelper {
  protected val containerPath = Paths.get("/assignment")

  def run(assignment: Assignment): EitherT[Future, Exception, AssignmentCheckResult] =
    for {
      paths <- fileSystemPreparation(assignment)
      (basePath: Path, jarFilePath: Path) = paths
      _ <- runDockerContainerAndWait(basePath, jarFilePath)
      json <- readAssignmentResult(basePath)
      entity <- decode[AssignmentCheckResult](json)
        .toEitherT[Future]
        .leftMap(InternalError(_))
        .leftWiden[Exception]
    } yield entity

  private def fileSystemPreparation(
    assignment: Assignment
  ): EitherT[Future, Exception, (Path, Path)] = {
    val basePath = generatePath(assignment.userInformation)
    val jarFilePath = basePath.resolve(assignment.fileDescription.fileName)
    FileAssistant.makeDirectoryPath(basePath.toString)
    StreamHelper.streamToFile(
      assignment.fileDescription.file,
      jarFilePath
    ).map(_ => (basePath, jarFilePath))
  }

  private def runDockerContainerAndWait(
    basePath: Path,
    jarFilePath: Path
  ): EitherT[Future, Exception, InspectContainerResult] =
    DockerHelper.dockerInteractionCommandWrapper { dockerManager =>
      createContainer(dockerManager.dockerCommandExecutor, basePath, jarFilePath).flatMap(containerId => {
        val result = for {
          _ <- startContainer(dockerManager.dockerCommandExecutor, containerId)
          state <- DockerHelper.waitCompletionDockerContainer(containerId, delays)(dockerManager)
        } yield state
        result.value.onComplete({
          case _ => DockerHelper.stopAndReleaseDockerContainer(containerId)(dockerManager)
        })
        result
      })
    }

  private def createContainer(
    dockerCommandExecutor: DockerCommandExecutor,
    basePath: Path,
    jarFilePath: Path
  ): EitherT[Future, Exception, String] =
    EitherT.apply(dockerCommandExecutor
      .createContainer(buildDockerContainer(basePath, jarFilePath))
      .map(_.asRight[Exception])
      .recover({
        case ex: Exception => ex.asLeft[String]
      }))

  private def buildDockerContainer(basePath: Path, jarFilePath: Path): DockerContainer =
    DockerContainer.apply(
      imageName,
      env = buildEnvVariables(jarFilePath),
      volumeMappings = Seq(buildVolumeMapping(basePath))
    )

  private def buildEnvVariables(jarFilePath: Path): Seq[String] =
    Seq(
      buildEnvVariable("ASSIGNMENT_JAR_PATH", containerPath.resolve(jarFilePath.getFileName).toString),
      buildEnvVariable("OUTPUT_PATH", containerPath.toString))

  private def startContainer(
    dockerCommandExecutor: DockerCommandExecutor,
    containerId: String
  ): EitherT[Future, Exception, Unit] =
    EitherT.apply(dockerCommandExecutor.startContainer(containerId)
      .map(_.asRight[Exception])
      .recover({
        case ex: Exception => ex.asLeft[Unit]
      }))

  private def readAssignmentResult(basePath: Path): EitherT[Future, Exception, String] =
    EitherT.fromEither[Future](FileAssistant.readMessageFromFile(basePath.resolve(resultFileName).toString))

}

package com.eg.plugin.action.submit

import java.io.FileNotFoundException

import cats.data.EitherT
import cats.implicits._
import com.eg.assignment.common.json.JsonFormats.assignmentCheckResultCodec
import com.eg.assignment.common.model.assignment.{Assignment, FileDescription, UserInformation}
import com.eg.assignment.common.model.result.AssignmentCheckResult
import com.eg.plugin.action.AssignmentStubHelper
import com.eg.plugin.action.submit.http.AssignmentHttpResource
import com.eg.plugin.config.PluginConfiguration
import com.eg.plugin.exception.SbtCommandFailureException
import com.eg.plugin.util.{FileSystemHelper, NotificationHelper, PropertyHelper}
import com.intellij.diagnostic.{LogMessage, MessagePool}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.circe.syntax._
import org.jetbrains.sbt.shell.SbtShellCommunication

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SubmitExecutor extends AssignmentStubHelper {
  private val jarExtension = "jar"
  private val jarFolderName = "target"

  def submitAssignment(
                        project: Project,
                        userInformation: UserInformation,
                        sbtCommand: String
                      ): Unit =
    (for {
      _ <- executeCommand(project, sbtCommand)
      assignment <- EitherT.fromEither[Future](buildAssignment(project, userInformation))
      assignmentCheckResult <- EitherT.fromEither[Future](AssignmentHttpResource.post(assignment))
    } yield updateProjectPropertyAndNotify(project, assignmentCheckResult))
      .leftMap(throw _)
      .value
      .recover({
        case ex =>
          MessagePool
            .getInstance()
            .addIdeFatalMessage(
              LogMessage.createEvent(
                ex,
                """No one's perfect, there's been an internal mistake.
                  |You should probably disable the plugin and let us know about incident.""".stripMargin
              )
            )
      })

  protected def updateProjectPropertyAndNotify(
                                                project: Project,
                                                result: AssignmentCheckResult
                                              ): Unit = {
    PropertyHelper
      .setProperty(
        project,
        PluginConfiguration.Properties.assignmentCheckResult,
        result.asJson.noSpaces
      )
    NotificationHelper.createTestAssignmentResultsNotification(project)
  }

  protected def executeCommand(
                                project: Project,
                                command: String
                              ): EitherT[Future, SbtCommandFailureException, String] =
    EitherT(SbtShellCommunication
      .forProject(project)
      .command(command).map(output =>
      if (!output.contains("[error]"))
        output.asRight
      else
        new SbtCommandFailureException(s"Command - $command, Output - $output").asLeft
    ))

  protected def buildAssignment(
                                 project: Project,
                                 userInformation: UserInformation
                               ): Either[Exception, Assignment] =
    for {
      jarFile <- tryToFindJar(project)
      projectName <- getProjectNameProperty(project)
    } yield Assignment(
      userInformation,
      FileDescription(
        jarFile.getName,
        jarFile.contentsToByteArray()
      ),
      projectName
    )

  protected def tryToFindJar(
                              project: Project
                            ): Either[FileNotFoundException, VirtualFile] =
    (for {
      projectFolder <- FileSystemHelper.getVirtualFile(project.getBasePath)
      jarFileFolder <- projectFolder.getChildren.find(_.getName.equals(jarFolderName))
      jarFile <- {
        jarFileFolder.getFileSystem.refresh(false)
        jarFileFolder.getChildren.find(children =>
          Option(children.getExtension)
            .map(_.equals(jarExtension))
            .getOrElse(false))
      }
    } yield jarFile).fold[Either[FileNotFoundException, VirtualFile]](
      new FileNotFoundException(
        "Couldn't find the file by the specified path - ./target/*.jar. Could you please check assembly build settings."
      ).asLeft)(_.asRight)
}

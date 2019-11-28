package com.eg.plugin.action

import java.io.FileNotFoundException

import cats.implicits._
import com.eg.plugin.config.PluginConfiguration
import com.eg.plugin.util.{FileSystemHelper, PropertyHelper}
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.EmptyIcon
import org.jetbrains.plugins.scala.extensions.inWriteAction

class ProjectReinitializer extends AnAction with AssignmentStubHelper {
  private val excludeFiles: Seq[String] = Seq(".idea")

  override def update(anActionEvent: AnActionEvent): Unit =
    anActionEvent
      .getPresentation()
      .setVisible(
        PropertyHelper.isPropertySet(anActionEvent.getProject,
          PluginConfiguration.Properties.projectName))

  override def actionPerformed(anActionEvent: AnActionEvent): Unit =
    reinitialize(anActionEvent.getProject)

  protected def reinitialize(project: Project): Unit =
    if (isActionApprovedByUser(project))
      (FileSystemHelper.getVirtualFile(project.getBasePath) match {
        case Some(projectDirectory) =>
          inWriteAction {
            for {
              _ <- FileSystemHelper.clearDirectory(projectDirectory, excludeFiles)
              pathToStubProject <- getPathToStubProject(project)
              result <- copyAssignmentStubToDirectory(pathToStubProject, projectDirectory)
            } yield result
          }

        case None => new FileNotFoundException(
          s"An error occurred when trying to read the project."
        ).asLeft
      }).leftMap(throw _)

  protected def isActionApprovedByUser(project: Project): Boolean =
    Messages.showOkCancelDialog(
      project,
      ProjectReinitializer.Message.dialogMessage,
      ProjectReinitializer.Message.title,
      Messages.YES_BUTTON,
      Messages.NO_BUTTON,
      EmptyIcon.ICON_8
    ) match {
      case 0 => true
      case _ => false
    }
}

object ProjectReinitializer {
  object Message {
    val title = "Project Reinitialization"
    val dialogMessage =
      "The project will be reinitialized and all changes will be lost. Would you like to continue?"
  }
}

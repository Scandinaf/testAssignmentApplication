package com.eg.plugin.action

import java.util

import cats.implicits._
import com.eg.plugin.action.ui.ChooseProjectDialog
import com.eg.plugin.config.{AssignmentConfiguration, PluginConfiguration}
import com.eg.plugin.exception.FileNotChosenException
import com.eg.plugin.util.{FileSystemHelper, PropertyHelper, SbtHelper}
import com.intellij.diagnostic.{LogMessage, MessagePool}
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.fileChooser.{FileChooserDescriptorFactory, FileChooserFactory}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer
import org.jetbrains.plugins.scala.extensions.inWriteAction
import org.jetbrains.sbt.project.SbtProjectOpenProcessor

import scala.collection.JavaConverters._

class ProjectImporter extends AnAction with AssignmentStubHelper {

  override def actionPerformed(anActionEvent: AnActionEvent): Unit =
    ChooseProjectDialog(
      AssignmentConfiguration.assignmentsDescription
    ) match {
      case dialog =>
        if (dialog.showAndGet()) {
          val selectedValue = dialog.getSelectedValue
          showFileChooserDialog(
            selectedValue.projectName,
            selectedValue.path,
            anActionEvent.getProject
          )
        }
    }

  protected def showFileChooserDialog(
                                       projectName: String,
                                       path: String,
                                       project: Project
                                     ): Unit =
    FileChooserFactory
      .getInstance()
      .createPathChooser(
        FileChooserDescriptorFactory.createSingleFolderDescriptor(),
        project,
        null
      ).choose(null, getFileChooserConsumer(path, projectName))

  protected def getFileChooserConsumer(
                                        path: String,
                                        projectName: String
                                      ): Consumer[_ >: util.List[VirtualFile]] =
    (virtualFiles: util.List[VirtualFile]) =>
      (virtualFiles.asScala.toList.headOption match {
        case Some(vf) =>
          inWriteAction {
            for {
              folder <- FileSystemHelper.createUniqueDirectory(vf, projectName)
              _ <- copyAssignmentStubToDirectory(path, folder)
              _ <- SbtHelper.changeSbtSetting(folder, "\\$projectName", folder.getName)
            } yield folder
          }.map(openImportProject(_, projectName))
        case None =>
          new FileNotChosenException(
            """It's an unexpected situation.
              |Somehow managed not to choose any files in the dialog.""".stripMargin
          ).asLeft
      }).recover {
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
      }

  protected def openImportProject(
                                   virtualFile: VirtualFile,
                                   projectName: String
                                 ): Unit =
    Option(new SbtProjectOpenProcessor()
      .doOpenProject(
        virtualFile,
        null,
        true
      )) match {
      case Some(project) => PropertyHelper.setProperty(
        project,
        PluginConfiguration.Properties.projectName,
        projectName
      )
      case None => inWriteAction {
        virtualFile.delete(None)
      }
    }
}

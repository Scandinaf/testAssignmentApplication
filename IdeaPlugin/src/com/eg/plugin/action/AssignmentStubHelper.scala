package com.eg.plugin.action

import java.io.{FileNotFoundException, IOException}

import cats.implicits._
import com.eg.plugin.config.{AssignmentConfiguration, PluginConfiguration}
import com.eg.plugin.exception.PropertyNotFoundException
import com.eg.plugin.util.{FileSystemHelper, PropertyHelper}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile


trait AssignmentStubHelper {
  protected def copyAssignmentStubToDirectory(
                                               pathToStubProject: String,
                                               destination: VirtualFile,
                                             ): Either[IOException, Unit] =
    FileSystemHelper.getVirtualFileFromResources(pathToStubProject) match {
      case Some(projectFolder) => FileSystemHelper.copyDirectoryContent(projectFolder, destination)
      case None => new FileNotFoundException(
        s"An error occurred when trying to read the project's stub. Path - $pathToStubProject"
      ).asLeft
    }

  protected def getPathToStubProject(project: Project): Either[PropertyNotFoundException, String] =
    getProjectNameProperty(project)
      .map(AssignmentConfiguration.getProjectPath(_))

  protected def getProjectNameProperty(project: Project): Either[PropertyNotFoundException, String] =
    PropertyHelper.getPropertyEither(
      project,
      PluginConfiguration.Properties.projectName
    )
}

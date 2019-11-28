package com.eg.plugin.action.submit

import java.io.FileNotFoundException
import java.lang.reflect.Method
import java.net.{URL, URLClassLoader}
import java.util

import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.plugin.config.PluginConfiguration
import com.eg.plugin.util.FileSystemHelper
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.ide.plugins.{IdeaPluginDescriptorImpl, PluginManager}
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project

import scala.collection.JavaConverters._


// Reasons of this solution you can find within the following link.
// https://intellij-support.jetbrains.com/hc/en-us/community/posts/360005592220-LinkageError-loader-constraint-violation-loader-instance-of-com-intellij-ide-plugins-cl-PluginClassLoader-previously-initiated-loading-for-a-different-type-with-name-scala-concurrent-Future-
object SubmitExecutorFactory {
  private val libFolderPath = "/lib"
  private val myUrlsFieldName = "myURLs"
  private val scalaPluginId = "org.intellij.scala"
  private val methodName = "submitAssignment"
  private val customClassLoader = buildCustomClassLoader
  private val submitAssignmentMethod = buildReflectionMethod

  def submitAssignment(project: Project, userInfo: UserInformation): Unit =
    submitAssignmentMethod._1.invoke(
      submitAssignmentMethod._2,
      project,
      userInfo.nickname,
      userInfo.mailbox
    )

  private def buildReflectionMethod: (Method, AnyRef) = {
    val cls = customClassLoader.loadClass(SubmitExecutor.getClass.getName)
    val instance = cls.getField("MODULE$").get(cls)
    val method = cls.getMethod(methodName, classOf[Project], classOf[String], classOf[String])
    (method, instance)
  }

  private def buildCustomClassLoader =
    new URLClassLoader(
      getInternalClassPathUnsafe +: convertLibFolderToUrls,
      PluginManager.getPlugin(PluginId.getId(scalaPluginId))
        .asInstanceOf[IdeaPluginDescriptorImpl]
        .getPluginClassLoader
    )

  private def convertLibFolderToUrls: Array[URL] =
    FileSystemHelper
      .getVirtualFileFromResources(libFolderPath) match {
      case Some(libFolder) => libFolder.getChildren.map(jarFile => new URL(jarFile.getUrl))
      case None            =>
        throw new FileNotFoundException(
          """It's an unexpected situation.
            |There is no directory with external dependencies.""".stripMargin)
    }

  private def getInternalClassPathUnsafe: URL = {
    val pluginClassLoader = PluginManager
      .getPlugin(PluginId.getId(PluginConfiguration.pluginId))
      .asInstanceOf[IdeaPluginDescriptorImpl]
      .getPluginClassLoader.asInstanceOf[PluginClassLoader]
    val field = pluginClassLoader.getClass.getSuperclass.getDeclaredField(myUrlsFieldName)
    field.setAccessible(true)
    field.get(pluginClassLoader).asInstanceOf[util.ArrayList[URL]].asScala.toList.head
  }
}

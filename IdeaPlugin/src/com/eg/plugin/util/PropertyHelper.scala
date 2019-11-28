package com.eg.plugin.util

import cats.implicits._
import com.eg.plugin.exception.PropertyNotFoundException
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

object PropertyHelper {

  def isPropertySet(project: Project, propertyName: String): Boolean =
    getPropertiesComponent(project)
      .isValueSet(propertyName)

  def setProperty(project: Project, propertyName: String, value: String): Unit =
    getPropertiesComponent(project)
      .setValue(propertyName, value)

  def getProperty(project: Project, propertyName: String): Option[String] =
    if (isPropertySet(project, propertyName))
      getPropertiesComponent(project)
        .getValue(propertyName).some
    else None

  def getPropertyEither(
    project: Project,
    propertyName: String
  ): Either[PropertyNotFoundException, String] =
    PropertyHelper.getProperty(project, propertyName) match {
      case Some(propertyValue) => propertyValue.asRight
      case None                =>
        new PropertyNotFoundException(
          "It's an unexpected situation. There is no requested property in the project configuration."
        ).asLeft
    }

  protected def getPropertiesComponent(project: Project) =
    PropertiesComponent
      .getInstance(project)
}
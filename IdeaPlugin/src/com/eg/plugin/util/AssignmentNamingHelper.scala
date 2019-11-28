package com.eg.plugin.util

import com.eg.plugin.config.PluginConfiguration
import com.eg.plugin.model.AssignmentDescription

object AssignmentNamingHelper {
  val projectNames = Array(
    AssignmentDescription(
      "calculator",
      "Calculator",
      "Simple task to implement the calculator"
    ),
  )

  def getProjectPath(projectName: String) =
    s"${ PluginConfiguration.pathToStub }/$projectName"
}

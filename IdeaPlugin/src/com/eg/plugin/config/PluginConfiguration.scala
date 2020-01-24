package com.eg.plugin.config

object PluginConfiguration {
  val pluginId = "com.eg.plugin.test_assignment"
  val pathToStub = "/test-assignment-stubs"
  val serverHost = "http://assignments.eg.local:8080"

  object Properties {
    val projectName = "testAssignmentPlugin.projectName"
    val assignmentCheckResult = "testAssignmentPlugin.assignmentCheckResult"
  }
}

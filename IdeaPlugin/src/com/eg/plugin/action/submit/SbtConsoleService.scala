package com.eg.plugin.action.submit

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import org.jetbrains.sbt.shell.{SbtProcessManager, SbtShellConsoleView}

class SbtConsoleService(project: Project) {
  private val sbtConsole = getSbtConsole

  def printMessage(msg: String): Unit =
    print(msg, ConsoleViewContentType.NORMAL_OUTPUT)

  def printErrorMessage(msg: String): Unit =
    print(msg, ConsoleViewContentType.ERROR_OUTPUT)

  protected def print(msg: String, contentType: ConsoleViewContentType): Unit = {
    sbtConsole.print(msg, contentType)
    sbtConsole.flushDeferredText()
  }

  protected def getSbtConsole: SbtShellConsoleView =
    SbtProcessManager.forProject(project).acquireShellRunner().getConsoleView
}

object SbtConsoleService {
  def apply(project: Project): SbtConsoleService = new SbtConsoleService(project)
}

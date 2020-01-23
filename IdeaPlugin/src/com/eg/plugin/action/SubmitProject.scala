package com.eg.plugin.action

import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.plugin.action.submit.SubmitExecutor
import com.eg.plugin.action.ui.UserForm
import com.eg.plugin.config.{AssignmentConfiguration, PluginConfiguration}
import com.eg.plugin.util.PropertyHelper
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.components.ProjectComponent

class SubmitProject extends AnAction with ProjectComponent {

  override def update(anActionEvent: AnActionEvent): Unit = {
    super.update(anActionEvent)
    anActionEvent
      .getPresentation()
      .setVisible(
        PropertyHelper.isPropertySet(anActionEvent.getProject,
          PluginConfiguration.Properties.projectName))
  }

  override def actionPerformed(anActionEvent: AnActionEvent): Unit =
    UserForm() match {
      case f =>
        if (f.showAndGet())
          userFormConsumer(f.getUserInformation, anActionEvent)
    }

  protected def userFormConsumer(
                                  userInfo: UserInformation,
                                  anActionEvent: AnActionEvent
                                ): Unit =
    for {
      projectName <- PropertyHelper.getProperty(anActionEvent.getProject, PluginConfiguration.Properties.projectName)
      assignmentConfiguration <- AssignmentConfiguration.assignmentsConfiguration.find(_.description.projectName == projectName)
    } yield SubmitExecutor.submitAssignment(
      anActionEvent.getProject,
      userInfo,
      assignmentConfiguration.sbtExecuteCommand
    )
}

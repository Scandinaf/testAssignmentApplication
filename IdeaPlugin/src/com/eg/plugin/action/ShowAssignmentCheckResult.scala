package com.eg.plugin.action

import com.eg.assignment.common.json.JsonFormats.assignmentCheckResultCodec
import com.eg.assignment.common.model.result.AssignmentCheckResult
import com.eg.plugin.action.ui.AssignmentCheckResultDialog
import com.eg.plugin.config.PluginConfiguration
import com.eg.plugin.util.PropertyHelper
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.project.Project
import io.circe.parser.decode

class ShowAssignmentCheckResult extends AnAction {

  override def update(anActionEvent: AnActionEvent): Unit =
    anActionEvent
      .getPresentation()
      .setVisible(
        PropertyHelper.isPropertySet(anActionEvent.getProject,
          PluginConfiguration.Properties.assignmentCheckResult))

  override def actionPerformed(anActionEvent: AnActionEvent): Unit =
    perform(anActionEvent.getProject)

  def perform(project: Project): Either[Exception, Unit] =
    for {
      json <- PropertyHelper.getPropertyEither(
        project,
        PluginConfiguration.Properties.assignmentCheckResult
      )
      result <- decode[AssignmentCheckResult](json)
    } yield AssignmentCheckResultDialog(result, project).show()

}

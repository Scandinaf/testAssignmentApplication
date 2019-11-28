package com.eg.plugin.util

import com.eg.plugin.action.ShowAssignmentCheckResult
import com.intellij.notification._
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import javax.swing.event.HyperlinkEvent

object NotificationHelper {
  private val notificationGroup =
    new NotificationGroup("Test Assignment Plugin Notification Group",
      NotificationDisplayType.BALLOON, true)

  def createTestAssignmentResultsNotification(project: Project): Unit =
    Notifications.Bus.notify(notificationGroup.createNotification(NotificationType.INFORMATION)
      .setTitle("Test Assignment Results")
      .setContent("The results of your assignment are now <a href='show_results'>available</a>")
      .setListener((notification: Notification, _: HyperlinkEvent) => {
        ActionManager.getInstance()
          .getAction("MyPlugin.ShowAssignmentCheckResult")
          .asInstanceOf[ShowAssignmentCheckResult].perform(project)
        notification.expire()
      }), project)
}

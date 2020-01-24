package com.eg.plugin.model

import com.eg.plugin.action.ui.model.ListEntity
import com.eg.plugin.config.AssignmentConfiguration.getProjectPath

case class AssignmentDescription(
                                  projectName: String,
                                  alias: String,
                                  description: String
                                ) extends ListEntity {
  private val maxTextSize = 38
  val path: String = getProjectPath(projectName)
  override val getHint: String = description
  override val getText: String =
    if (alias.size > maxTextSize)
      s"${alias.substring(0, maxTextSize)}..."
    else
      alias
}

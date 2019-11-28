package com.eg.plugin.model

import com.eg.plugin.action.ui.model.ListEntity
import com.eg.plugin.util.AssignmentNamingHelper.getProjectPath

case class AssignmentDescription(projectName: String, alias: String, description: String) extends ListEntity {
  override val getText: String =
    if (alias.size > 38)
      s"${ alias.substring(0, 38) }..."
    else
      alias
  override val getHint: String = description
  val path: String = getProjectPath(projectName)
}

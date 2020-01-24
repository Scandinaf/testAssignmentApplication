package com.eg.assignment.server.dao.model

import java.util.UUID

import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.assignment.common.model.result.AssignmentCheckResult

final case class DbAssignmentAttempt(
  id: UUID,
  projectName: String,
  user: UserInformation,
  epochSeconds: Long,
  result: AssignmentCheckResult,
)

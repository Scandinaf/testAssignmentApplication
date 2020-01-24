package com.eg.assignment.server.model

import java.time.ZonedDateTime
import java.util.UUID

import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.assignment.common.model.result.AssignmentCheckResult

final case class AssignmentAttempt(
  id: UUID,
  projectName: String,
  user: UserInformation,
  dateTime: ZonedDateTime,
  result: AssignmentCheckResult,
)
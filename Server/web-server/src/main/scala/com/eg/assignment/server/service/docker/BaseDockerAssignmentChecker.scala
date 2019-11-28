package com.eg.assignment.server.service.docker

import com.eg.assignment.common.model.result.AssignmentCheckResult
import com.eg.assignment.server.model.Assignment
import com.eg.assignment.server.service.AssignmentChecker

trait BaseDockerAssignmentChecker extends AssignmentChecker[Assignment, AssignmentCheckResult] {
  val imageName: String
}

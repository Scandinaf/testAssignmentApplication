package com.eg.assignment.server.service

import java.nio.file.{Path, Paths}

import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.assignment.server.Main.assignmentDirPath

trait AssignmentFileSystemHelper {
  protected def generatePath(userInformation: UserInformation): Path =
    Paths.get(
      assignmentDirPath,
      userInformation.nickname,
      s"${ System.currentTimeMillis() }_${ java.util.UUID.randomUUID() }"
    )
}

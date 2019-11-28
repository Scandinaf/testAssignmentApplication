package com.eg.assignment.server.model

import com.eg.assignment.common.model.assignment.UserInformation

case class Assignment(
  projectName: String,
  userInformation: UserInformation,
  fileDescription: FileDescription
)

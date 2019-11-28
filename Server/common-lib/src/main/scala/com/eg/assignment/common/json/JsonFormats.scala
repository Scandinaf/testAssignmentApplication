package com.eg.assignment.common.json

import com.eg.assignment.common.model.assignment.{Assignment, FileDescription, UserInformation}
import com.eg.assignment.common.model.http.HttpErrorResponse
import com.eg.assignment.common.model.result.{AssignmentCheckResult, TestResult, TestSuiteResult}
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object JsonFormats {
  implicit val assignmentCheckResultCodec: Codec[AssignmentCheckResult] = deriveCodec
  implicit val testSuiteResultCodec: Codec[TestSuiteResult] = deriveCodec
  implicit val testResultCodec: Codec[TestResult] = deriveCodec

  implicit val assignmentCodec: Codec[Assignment] = deriveCodec
  implicit val fileDescriptionCodec: Codec[FileDescription] = deriveCodec
  implicit val userInformationCodec: Codec[UserInformation] = deriveCodec

  implicit val httpErrorResponseCodec: Codec[HttpErrorResponse] = deriveCodec
}

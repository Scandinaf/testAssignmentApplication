package com.eg.assignment.common.model.result

case class AssignmentCheckResult(
  isPassed: Boolean,
  resultScore: Option[Int] = None,
  testSuiteResults: List[TestSuiteResult] = List.empty,
  additionalInformation: Option[String] = None
)
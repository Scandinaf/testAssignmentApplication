package com.eg.assignment.checker.service

import com.eg.assignment.ShortestPath
import com.eg.assignment.checker.service.test.ShortestPathTestSuite
import com.eg.assignment.common.model.result.TestSuiteResult
import com.eg.assignment.common.service.Checker

class AssignmentChecker(val instance: ShortestPath)
  extends Checker {

  private val testSuites = List(
    ShortestPathTestSuite(instance),
  )

  override lazy val getTestResults: List[TestSuiteResult] =
    testSuites.map(_.runTestSuite)
}

object AssignmentChecker {
  def apply(instance: ShortestPath): AssignmentChecker = new AssignmentChecker(instance)
}

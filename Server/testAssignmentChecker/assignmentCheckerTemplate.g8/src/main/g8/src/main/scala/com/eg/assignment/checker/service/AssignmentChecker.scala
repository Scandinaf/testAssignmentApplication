package com.eg.assignment.checker.service

import com.eg.assignment.checker.service.test.TestSuite
import com.eg.assignment.common.model.result.TestSuiteResult
import com.eg.assignment.common.service.Checker

class AssignmentChecker[T](val instance: T)
  extends Checker {

  private val testSuites: List[TestSuite[T]] = ???

  override lazy val getTestResults: List[TestSuiteResult] =
    testSuites.map(_.runTestSuite)
}

object AssignmentChecker {
  def apply[T](instance: T): AssignmentChecker[T] = new AssignmentChecker(instance)
}

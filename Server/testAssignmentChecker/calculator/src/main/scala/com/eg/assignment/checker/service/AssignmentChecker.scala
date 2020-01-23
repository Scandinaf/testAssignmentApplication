package com.eg.assignment.checker.service

import com.eg.assignment.Calculator
import com.eg.assignment.checker.service.test.{AdditionMethodTestSuite, DivisionMethodTestSuite, MultiplicationMethodTestSuite, SubtractionMethodTestSuite}
import com.eg.assignment.common.model.result.TestSuiteResult
import com.eg.assignment.common.service.Checker

class AssignmentChecker(val instance: Calculator)
  extends Checker {

  private val testSuites = List(
    AdditionMethodTestSuite(instance),
    SubtractionMethodTestSuite(instance),
    MultiplicationMethodTestSuite(instance),
    DivisionMethodTestSuite(instance),
  )

  override lazy val getTestResults: List[TestSuiteResult] =
    testSuites.map(_.runTestSuite)
}

object AssignmentChecker {
  def apply(instance: Calculator): AssignmentChecker = new AssignmentChecker(instance)
}

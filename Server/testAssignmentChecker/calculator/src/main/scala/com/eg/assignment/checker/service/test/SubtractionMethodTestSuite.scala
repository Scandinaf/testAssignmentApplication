package com.eg.assignment.checker.service.test

import cats.implicits._
import com.eg.assignment.Calculator
import com.eg.assignment.common.model.result.{TestResult, TestSuiteResult}

class SubtractionMethodTestSuite(val instance: Calculator) extends TestSuite[Calculator] {

  def runTestSuite: TestSuiteResult =
    TestSuiteResult.apply(
      "subtractionMethodTestSuite",
      runTestsWithErrorHandling(correctlySubtractionsNumbers),
      None
    )

  private def correctlySubtractionsNumbers(): TestResult = {
    val value1 = 1
    val value2 = 3
    val result_1 = instance.subtraction(value1, value2)
    val result_2 = instance.subtraction(value2, value1)
    (result_1, result_2) match {
      case (-2, 2) => TestResult.apply(isPassed = true)
      case _       => TestResult.apply(
        isPassed = false,
        hint = "Try the following example - (x - y) = ???".some
      )
    }
  }
}

object SubtractionMethodTestSuite {
  def apply(instance: Calculator): SubtractionMethodTestSuite = new SubtractionMethodTestSuite(instance)
}
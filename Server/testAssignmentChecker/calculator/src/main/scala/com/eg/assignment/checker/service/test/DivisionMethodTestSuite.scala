package com.eg.assignment.checker.service.test

import cats.implicits._
import com.eg.assignment.Calculator
import com.eg.assignment.common.model.result.{TestResult, TestSuiteResult}

class DivisionMethodTestSuite(val instance: Calculator) extends TestSuite[Calculator] {

  def runTestSuite: TestSuiteResult =
    TestSuiteResult.apply(
      "divisionMethodTestSuite",
      runTestsWithErrorHandling(correctlyDivisionsNumbers),
      None
    )

  private def correctlyDivisionsNumbers(): TestResult = {
    val result_1 = instance.division(6, 2)
    val result_2 = instance.division(9, 2)
    (result_1, result_2) match {
      case (3.0, 4.5) => TestResult.apply(isPassed = true)
      case _          => TestResult.apply(
        isPassed = false,
        hint = "Try the following example - (x / y) = ???".some
      )
    }
  }
}

object DivisionMethodTestSuite {
  def apply(instance: Calculator): DivisionMethodTestSuite = new DivisionMethodTestSuite(instance)
}

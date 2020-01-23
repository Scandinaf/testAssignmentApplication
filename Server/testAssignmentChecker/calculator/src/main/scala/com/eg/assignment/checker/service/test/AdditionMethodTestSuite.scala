package com.eg.assignment.checker.service.test

import cats.implicits._
import com.eg.assignment.Calculator
import com.eg.assignment.common.model.result.{TestResult, TestSuiteResult}

class AdditionMethodTestSuite(val instance: Calculator) extends TestSuite[Calculator] {

  def runTestSuite: TestSuiteResult =
    TestSuiteResult.apply(
      "additionMethodTestSuite",
      runTestsWithErrorHandling(correctlySumsNumbers),
      None
    )

  private def correctlySumsNumbers(): TestResult = {
    val value1 = 1
    val value2 = 3
    val result_1 = instance.addition(value1, value2)
    val result_2 = instance.addition(value2, value1)
    (result_1, result_2) match {
      case (4, 4) => TestResult.apply(isPassed = true)
      case _      => TestResult.apply(
        isPassed = false,
        hint = "Try the following example - (x + y) = ???".some
      )
    }
  }
}

object AdditionMethodTestSuite {
  def apply(instance: Calculator): AdditionMethodTestSuite = new AdditionMethodTestSuite(instance)
}
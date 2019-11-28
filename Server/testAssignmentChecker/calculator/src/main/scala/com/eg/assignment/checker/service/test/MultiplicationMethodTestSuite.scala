package com.eg.assignment.checker.service.test

import cats.implicits._
import com.eg.assignment.Calculator
import com.eg.assignment.common.model.result.{TestResult, TestSuiteResult}

class MultiplicationMethodTestSuite(val instance: Calculator) extends TestSuite[Calculator] {

  def runTestSuite: TestSuiteResult =
    TestSuiteResult.apply(
      "multiplicationMethodTestSuite",
      runTestsWithErrorHandling(correctlyMultiplicationsNumbers),
      None
    )

  private def correctlyMultiplicationsNumbers(): TestResult = {
    val value1 = 2
    val value2 = 3
    val result_1 = instance.multiplication(value1, value2)
    val result_2 = instance.multiplication(value2, value1)
    (result_1, result_2) match {
      case (6, 6) => TestResult.apply(isPassed = true)
      case _      => TestResult.apply(
        isPassed = false,
        hint = "Try the following example - (x * y) = ???".some
      )
    }
  }
}

object MultiplicationMethodTestSuite {
  def apply(instance: Calculator): MultiplicationMethodTestSuite = new MultiplicationMethodTestSuite(instance)
}

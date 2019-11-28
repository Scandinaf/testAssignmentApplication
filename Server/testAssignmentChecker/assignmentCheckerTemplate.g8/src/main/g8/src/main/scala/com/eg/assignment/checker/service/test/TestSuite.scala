package com.eg.assignment.checker.service.test

import cats.implicits._
import com.eg.assignment.checker.helper.CompanionHelper.tryAllExceptions
import com.eg.assignment.common.model.result.{TestResult, TestSuiteResult}
import com.typesafe.scalalogging.LazyLogging

trait TestSuite[T] extends LazyLogging {
  val instance: T

  def runTestSuite: TestSuiteResult

  protected def runTestsWithErrorHandling(
    tests: (() => TestResult)*
  ): List[TestResult] =
    tests.map(test => tryAllExceptions(test()).toEither.leftMap({
      case _: NotImplementedError | _: AbstractMethodError => TestResult.apply(
        isPassed = false,
        hint =
          """Unfortunately, we were unable to perform the
            |test because there is no implementation.
            |Check your code and send the task again.""".stripMargin.some
      )
      case ex                                              =>
        logger.error("An unforeseen situation has arisen within the framework of the annex.", ex)
        throw ex
    }).merge).toList
}

package com.eg.assignment.common.model.result

case class TestSuiteResult(
  name: String,
  isPassed: Boolean,
  resultScore: Option[Int] = None,
  hint: Option[String] = None,
  testResults: List[TestResult] = List.empty,
) extends Result

object TestSuiteResult {
  def apply(
    name: String,
    tests: List[TestResult],
    hint: Option[String]
  ): TestSuiteResult = buildTestSuiteResult(name, tests, hint)

  private def buildTestSuiteResult(
    name: String,
    tests: List[TestResult],
    hint: Option[String]
  ): TestSuiteResult = {
    val (isPassed, resultScore) = Result.calculateFinalResult(tests)
    TestSuiteResult.apply(
      name = name,
      isPassed = isPassed,
      resultScore = resultScore,
      testResults = tests,
      hint = hint,
    )
  }
}

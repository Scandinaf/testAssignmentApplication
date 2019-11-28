package com.eg.assignment.common.model.result

trait Result {
  def isPassed: Boolean
  def resultScore: Option[Int]
}

object Result {
  def calculateFinalResult(
    testResults: List[Result]
  ): (Boolean, Option[Int]) =
    testResults.foldLeft[(Boolean, Option[Int])]((true, None))((r, testResult) =>
      (
        calculateIsPassed(r._1, testResult.isPassed),
        calculateScore(r._2, testResult.resultScore)
      )
    )

  private def calculateIsPassed(prevResult: Boolean, result: Boolean): Boolean =
    prevResult && result

  private def calculateScore(prevScore: Option[Int], score: Option[Int]): Option[Int] =
    (prevScore, score) match {
      case (None, Some(score))            => Some(score)
      case (Some(prevScore), Some(score)) => Some(Math.round((prevScore.toDouble + score.toDouble) / 2).toInt)
      case _                              => prevScore
    }
}

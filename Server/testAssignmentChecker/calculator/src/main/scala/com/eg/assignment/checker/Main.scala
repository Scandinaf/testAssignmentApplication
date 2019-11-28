package com.eg.assignment.checker

import cats.implicits._
import com.eg.assignment.Calculator
import com.eg.assignment.checker.helper.MotivationalPhraseHelper
import com.eg.assignment.checker.service.{AssignmentChecker, ImplementationPreProcessor}
import com.eg.assignment.common.exception.ImplementationException
import com.eg.assignment.common.helper.{ENVVariableHelper, FileAssistant}
import com.eg.assignment.common.json.JsonFormats.assignmentCheckResultCodec
import com.eg.assignment.common.model.result.{AssignmentCheckResult, Result, TestSuiteResult}
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._

object Main extends App with LazyLogging {
  (for {
    outputPath <- ENVVariableHelper.getENVVariable("OUTPUT_PATH")
    ancestor = "com.eg.assignment.Calculator"
    assignmentCheckResult <- findAndTestAssignment(ancestor)
    assignmentCheckResultJson = assignmentCheckResult.asJson
    _ <- FileAssistant.writeMessageInFile(s"$outputPath/result.json", assignmentCheckResultJson.noSpaces)
  } yield assignmentCheckResultJson)
    .fold(logger.error(
      "An error occurred during the assignment check",
      _
    ), json => logger.info(json.toString))

  def findAndTestAssignment(ancestor: String): Either[Exception, AssignmentCheckResult] =
    (for {
      instance <- findImplementation(ancestor)
      testSuiteResults <- runTests(instance)
    } yield testSuiteResults)
      .fold({
        case ex @ (_: ImplementationException | _: IllegalArgumentException | _: ClassNotFoundException) =>
          AssignmentCheckResult(isPassed = false, additionalInformation = ex.getMessage.some).asRight
        case ex                                                                                          =>
          ex.asLeft
      }, buildAssignmentCheckResult(_).asRight)

  private def findImplementation(ancestor: String): Either[Exception, Calculator] =
    ImplementationPreProcessor(ancestor).getProcessResult

  private def runTests(instance: Calculator): Either[Exception, List[TestSuiteResult]] =
    AssignmentChecker(instance).getTestResults.asRight

  private def buildAssignmentCheckResult(
    testSuiteResults: List[TestSuiteResult]
  ): AssignmentCheckResult = {
    val (isPassed, resultScore) = Result.calculateFinalResult(testSuiteResults)
    AssignmentCheckResult(
      isPassed = isPassed,
      resultScore = resultScore,
      testSuiteResults,
      buildAdditionalInformation(isPassed).some
    )
  }

  private def buildAdditionalInformation(
    isPassed: Boolean
  ): String =
    if (isPassed)
      MotivationalPhraseHelper.generateMotivationalPhrase
    else
      MotivationalPhraseHelper.generateEncouragingPhrase
}

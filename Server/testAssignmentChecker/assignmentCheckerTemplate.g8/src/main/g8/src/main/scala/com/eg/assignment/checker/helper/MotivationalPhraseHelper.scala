package com.eg.assignment.checker.helper

import scala.util.Random

object MotivationalPhraseHelper {
  private val random = new Random
  private val motivationalPhrases = List(
    "Great Job!!!",
    "Well Done!!!",
    "Excellent job!!!",
  )

  private val encouragingPhrases = List(
    "Come on! You can do it!!!",
    "Don't give up!!!",
    "Keep fighting!!!",
  )

  def generateMotivationalPhrase: String =
    motivationalPhrases(random.nextInt(motivationalPhrases.length))

  def generateEncouragingPhrase: String =
    encouragingPhrases(random.nextInt(encouragingPhrases.length))
}

package com.eg.assignment.common.model.result

case class TestResult(
  name: Option[String] = None,
  isPassed: Boolean,
  resultScore: Option[Int] = None,
  executionTime: Option[Long] = None,
  hint: Option[String] = None,
) extends Result

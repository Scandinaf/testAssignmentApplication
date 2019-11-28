package com.eg.assignment.common.service

import com.eg.assignment.common.model.result.TestSuiteResult

trait Checker {
  def getTestResults: List[TestSuiteResult]
}

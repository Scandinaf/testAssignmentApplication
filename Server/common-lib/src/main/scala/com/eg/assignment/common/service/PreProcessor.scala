package com.eg.assignment.common.service

trait PreProcessor[T] {
  def getProcessResult: T
}

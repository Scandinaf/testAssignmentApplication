package com.eg.assignment.server.service

import cats.data.EitherT

import scala.concurrent.Future

trait AssignmentChecker[T, R] {
  def run(entity: T): EitherT[Future, Exception, R]
}

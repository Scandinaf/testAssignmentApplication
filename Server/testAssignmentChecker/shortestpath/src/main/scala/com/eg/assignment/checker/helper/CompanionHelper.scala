package com.eg.assignment.checker.helper

import scala.util.{Failure, Success, Try}

object CompanionHelper {
  /*
    Within the framework of this application we need to correctly process LinkageError and other fatal, according to the original Try, errors.
    Be extremely careful!!!
   */
  def tryAllExceptions[T](f: => T): Try[T] =
    try Success(f) catch {
      case ex: Throwable => Failure(ex)
    }
}

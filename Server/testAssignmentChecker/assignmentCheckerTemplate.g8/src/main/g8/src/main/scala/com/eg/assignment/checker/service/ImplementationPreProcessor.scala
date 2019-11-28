package com.eg.assignment.checker.service

import java.util.zip.ZipException

import cats.implicits._
import com.eg.assignment.checker.helper.CompanionHelper.tryAllExceptions
import com.eg.assignment.common.exception.ImplementationException
import com.eg.assignment.common.helper.{ClassFinderHelper, ClassInstanceHelper}
import com.eg.assignment.common.service.PreProcessor
import com.typesafe.scalalogging.LazyLogging

class ImplementationPreProcessor[T](ancestor: String) extends PreProcessor[Either[Exception, T]] with LazyLogging {
  override val getProcessResult: Either[Exception, T] = {
    tryAllExceptions(ClassFinderHelper.findSubclasses(ancestor) match {
      case Nil              => new ImplementationException(s"Unfortunately, no implementation has been found. Ancestor - \$ancestor").asLeft
      case classInfo :: Nil => ClassInstanceHelper.newInstance[T](classInfo.name)
      case list             => new ImplementationException(
        s"Unfortunately, too many implementations were found. Ancestor - \$ancestor, Implementations - \${ list.map(_.name) }"
      ).asLeft
    }).toEither.leftMap({
      case _: ZipException  =>
        new ImplementationException(
          "There was a problem with the jar file. Make sure everything is okay and resend the task."
        )
      case initializerError: ExceptionInInitializerError
        if Option(
          initializerError.getCause
        ).map(_.getMessage.contains("Scala signature package has wrong version"))
          .getOrElse(false) =>
        new ImplementationException(
          """Within our system we use the Scala version 2.12.10.
            |Please make sure that you use the same version or earlier""".stripMargin
        )
      case ex               =>
        logger.error("An unforeseen situation has arisen within the framework of the annex.", ex)
        throw ex
    }).flatten
  }
}

object ImplementationPreProcessor {
  def apply[T](ancestor: String): ImplementationPreProcessor[T] =
    new ImplementationPreProcessor[T](ancestor)
}

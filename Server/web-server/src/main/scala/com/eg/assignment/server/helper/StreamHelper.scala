package com.eg.assignment.server.helper

import java.nio.file.{FileAlreadyExistsException, Path}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import cats.data.EitherT
import cats.implicits._
import com.eg.assignment.server.Main.{executionContext, materializer}

import scala.concurrent.Future


object StreamHelper {
  def streamToFile(
    byteString: ByteString,
    filePath: Path
  ): EitherT[Future, Exception, IOResult] = {
    val file = filePath.toFile
    EitherT.apply(
      if (!file.exists)
        Source.single(byteString).runWith(FileIO.toPath(filePath)).map(_.asRight)
      else
        Future.successful(new FileAlreadyExistsException(s"FilePath - $filePath").asLeft)
    )
  }
}

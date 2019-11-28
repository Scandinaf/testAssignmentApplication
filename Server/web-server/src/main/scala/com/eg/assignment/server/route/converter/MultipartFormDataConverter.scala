package com.eg.assignment.server.route.converter

import akka.http.scaladsl.model.Multipart
import akka.util.ByteString
import cats.data.EitherT
import com.eg.assignment.common.json.JsonFormats.userInformationCodec
import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.assignment.server.Main.executionContext
import com.eg.assignment.server.model.{Assignment, FileDescription}

import scala.concurrent.Future
import scala.concurrent.duration._

object MultipartFormDataConverter {
  implicit class MultipartFormDataCompanion(data: Multipart.FormData) {
    def as[T: DataConverter]: EitherT[Future, Exception, T] = implicitly[DataConverter[T]].convert(data)
  }

  sealed trait DataConverter[T] extends MultipartDataHelper {
    def convert(data: Multipart.FormData): EitherT[Future, Exception, T]
  }

  implicit val assignmentWithStreamConverter = new DataConverter[Assignment] {
    override def convert(data: Multipart.FormData): EitherT[Future, Exception, Assignment] =
      EitherT.apply(getAllParts(data, 2 seconds).map(parameters =>
        for {
          projectName <- get[String](parameters, "projectName")
          userInformation <- getEntityByJson[UserInformation](parameters, "userInformation")
          fileDescriptionT <- get[(String, ByteString)](parameters, "jarFile")
        } yield Assignment(
          projectName,
          userInformation,
          FileDescription(
            fileDescriptionT._1,
            fileDescriptionT._2
          )
        )
      ))
  }
}

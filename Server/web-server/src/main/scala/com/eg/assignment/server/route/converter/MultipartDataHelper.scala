package com.eg.assignment.server.route.converter

import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.model.Multipart.BodyPart
import akka.util.ByteString
import cats.implicits._
import com.eg.assignment.server.Main.{executionContext, materializer}
import com.eg.assignment.server.exception.ParameterNotFoundException
import io.circe.Decoder
import io.circe.parser.decode

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait MultipartDataHelper {
  protected def get[T](
    parameters: Map[String, Any],
    key: String,
  ): Either[ParameterNotFoundException, T] =
    if (parameters.isDefinedAt(key))
      parameters(key).asInstanceOf[T].asRight
    else
      new ParameterNotFoundException(
        s"The data is in an incorrect state. Parameter '$key' not found."
      ).asLeft

  protected def getEntityByJson[T: Decoder](
    parameters: Map[String, Any],
    key: String
  ): Either[Exception, T] =
    get[String](parameters, key)
      .flatMap(json => decode[T](json))

  def getAllParts(
    data: Multipart.FormData,
    timeout: FiniteDuration,
    parallelism: Int = 1,
  ): Future[Map[String, Any]] =
    data.parts.mapAsync[(String, Any)](parallelism) {
      case b: BodyPart if b.filename.isDefined =>
        b.entity.dataBytes.runFold(ByteString.newBuilder)((builder, byteString) => builder.append(byteString))
          .map(builder =>
            b.name -> (b.filename.get, builder.result()))
      case b: BodyPart                         =>
        b.toStrict(timeout)
          .map(strict => b.name -> strict.entity.data.utf8String)
    }.runFold(Map.empty[String, Any])((map, tuple) => map + tuple)
}

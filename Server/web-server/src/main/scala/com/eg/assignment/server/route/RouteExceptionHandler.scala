package com.eg.assignment.server.route


import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, StandardRoute}
import com.eg.assignment.common.json.JsonFormats.httpErrorResponseCodec
import com.eg.assignment.common.model.http.HttpErrorResponse
import io.circe.syntax._
import io.circe.{Encoder, Json}

trait RouteExceptionHandler extends Directives {
  def buildInternalServerError(message: String): StandardRoute =
    buildResponse(
      StatusCodes.InternalServerError,
      HttpErrorResponse(message)
    )

  def buildBadRequest(message: String): StandardRoute =
    buildResponse(
      StatusCodes.BadRequest,
      HttpErrorResponse(message)
    )

  def buildOK[T: Encoder](entity: T): StandardRoute =
    buildResponse(
      StatusCodes.OK,
      entity
    )

  private def buildResponse[T: Encoder](
    statusCode: StatusCode,
    entity: T
  ): StandardRoute =
    complete(
      statusCode,
      buildJsonHttpEntity(entity.asJson)
    )

  private def buildJsonHttpEntity(json: Json) =
    HttpEntity(
      ContentTypes.`application/json`,
      json.noSpaces
    )
}

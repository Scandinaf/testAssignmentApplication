package com.eg.plugin.action.submit.http

import cats.implicits._
import com.eg.assignment.common.json.JsonFormats.{assignmentCheckResultCodec, userInformationCodec}
import com.eg.assignment.common.model.assignment.Assignment
import com.eg.assignment.common.model.result.AssignmentCheckResult
import com.eg.plugin.action.submit.http.exception.HttpError
import com.eg.plugin.config.PluginConfiguration
import io.circe.parser.decode
import io.circe.syntax._
import scalaj.http.{HttpResponse, MultiPart}

import scala.util.Try


object AssignmentHttpResource extends HttpResource[Assignment, Throwable, AssignmentCheckResult] {
  override val host: String = PluginConfiguration.serverHost
  override val basePath: String = "docker/assignment/check"
  override def post(entity: Assignment): Either[Throwable, AssignmentCheckResult] =
    Try(postRequest(entity)).toEither.flatMap({
      case resp if resp.code == 200 =>
        decode[AssignmentCheckResult](resp.body).flatMap(_.asRight)
      case resp                     =>
        HttpError(
          s"""There was a problem with http data transmission.
             |Status Code - ${ resp.code }.
             |Response - $resp.""".stripMargin).asLeft
    })

  protected def postRequest(assignment: Assignment): HttpResponse[String] =
    buildHttpRequest
      .timeout(connTimeoutMs = 2000, readTimeoutMs = 60000)
      .postForm(buildFormParams(assignment))
      .postMulti(buildMultiPart(assignment))
      .asString

  protected def buildFormParams(assignment: Assignment): Seq[(String, String)] =
    Seq(
      "userInformation" -> assignment.userInformation.asJson.noSpaces,
      "projectName" -> assignment.projectName,
    )

  private val mimeType = "application/octet-stream"
  protected def buildMultiPart(assignment: Assignment): MultiPart =
    MultiPart.apply(
      "jarFile",
      assignment.fileDescription.fileName,
      mimeType,
      assignment.fileDescription.file,
    )
}

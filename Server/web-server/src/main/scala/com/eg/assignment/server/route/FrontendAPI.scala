package com.eg.assignment.server.route

import akka.http.scaladsl.server._
import akka.http.scaladsl.marshalling.{PredefinedToEntityMarshallers, ToEntityMarshaller, ToResponseMarshallable}
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.MediaTypes._
import com.eg.assignment.server.pages.html._
import com.eg.assignment.server.service.LeaderboardService
import com.eg.assignment.server.Main.executionContext
import com.eg.assignment.server.service.LeaderboardService.ServiceError
import com.typesafe.scalalogging.LazyLogging
import play.twirl.api.{Html, HtmlFormat}
import FrontendAPI._

class FrontendAPI(leaderboardService: LeaderboardService) extends Directives with LazyLogging {
  val routes: Route = pathSingleSlash {
    get {
      complete {
        leaderboardService.fetchAllProjectNames.map(_.renderWith(Challenges(_)))
      }
    }
  } ~ path("leaderboard") {
    get {
      parameters(
        'projectName.as[String],
        'from.as[Long].?,
        'to.as[Long].?,
        'limit.as[Int].?,
        'offset.as[Int].?
      ) { (projectName, from, to, limit, offset) =>
        complete {
          leaderboardService.fetchLeaders(projectName, from, to, limit, offset)
            .map(_.renderWith(Leaderboard(projectName, _))
          )
        }
      }
    }
  }
}

object FrontendAPI {
  implicit class ServiceErrorHandler[T](val response: Either[ServiceError, T]) extends AnyVal {
    def renderWith(t: T => HtmlFormat.Appendable): ToResponseMarshallable = {
      response.fold(
        err => s"<h3>Error occurred</h3><p>$err</p>",
        data => t(data)
      )
    }
  }

  implicit val twirlHtmlMarshaller: ToEntityMarshaller[Html] = twirlMarshaller[Html](`text/html`)

  // taken from https://github.com/btomala/akka-http-sample/blob/master/src/main/scala/marshaller/TwirlMarshalling.scala
  private def twirlMarshaller[A <: AnyRef: Manifest](mediaTypes: MediaType.WithOpenCharset): ToEntityMarshaller[A] =
    PredefinedToEntityMarshallers.stringMarshaller(mediaTypes)
      .compose(_.toString)
}

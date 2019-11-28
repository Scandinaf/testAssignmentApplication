package com.eg.plugin.action.submit.http

import scalaj.http.{Http, HttpRequest}

trait HttpResource[In, OutL, OutR] {
  protected val host: String
  protected val basePath: String

  def post(entity: In): Either[OutL, OutR]

  protected def buildHttpRequest: HttpRequest =
    Http(s"$host/$basePath")
}

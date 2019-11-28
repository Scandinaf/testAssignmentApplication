package com.eg.plugin.action.submit.http.exception

case class HttpError(message: String) extends Exception(message)

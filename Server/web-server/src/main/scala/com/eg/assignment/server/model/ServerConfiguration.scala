package com.eg.assignment.server.model

import com.typesafe.config.Config

case class ServerConfiguration(host: String, port: Int) {}

object ServerConfiguration {
  object Field {
    val host = "host"
    val port = "port"
  }

  def apply(config: Config): ServerConfiguration =
    new ServerConfiguration(
      config.getString(Field.host),
      config.getInt(Field.port)
    )
}

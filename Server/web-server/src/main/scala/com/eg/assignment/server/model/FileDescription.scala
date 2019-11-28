package com.eg.assignment.server.model

import akka.util.ByteString

case class FileDescription(fileName: String, file: ByteString)

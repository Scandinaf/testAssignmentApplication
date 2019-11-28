package com.eg.assignment.common.helper

import java.io._
import java.nio.file.AccessDeniedException

import scala.io.Source

object FileAssistant {

  def makeDirectoryPath(path: String): Boolean =
    new File(path).mkdirs()

  def writeBytesInFile(path: String, bytes: Array[Byte]): Either[AccessDeniedException, Unit] =
    writeInFile(path, writeBytesUnsafe(path, bytes))

  def writeMessageInFile(path: String, message: String): Either[AccessDeniedException, Unit] =
    writeInFile(path, writeMessageUnsafe(path, message))

  def readMessageFromFile(path: String): Either[AccessDeniedException, String] =
    if (checkFileAllowForReading(path))
      Right(readMessageUnsafe(path))
    else
      Left(new AccessDeniedException(s"Unfortunately, it wasn't possible to get access to the file. Path - $path"))

  def checkFileAllowForRecording(path: String): Boolean = {
    val file = new File(path)
    !file.exists() || (file.isFile && file.canWrite)
  }

  def checkFileAllowForReading(path: String): Boolean = {
    val file = new File(path)
    !file.exists() || (file.isFile && file.canRead)
  }

  private def writeInFile[T](path: String, wFunction: () => Unit): Either[AccessDeniedException, Unit] =
    if (checkFileAllowForRecording(path))
      Right(wFunction())
    else
      Left(new AccessDeniedException(s"Unfortunately, it wasn't possible to get access to the file. Path - $path"))

  private def writeBytesUnsafe(path: String, bytes: Array[Byte])(): Unit =
    new BufferedOutputStream(new FileOutputStream(path)) {
      write(bytes)
      close()
    }

  private def writeMessageUnsafe(path: String, message: String)(): Unit =
    new PrintWriter(path) {
      write(message)
      close()
    }

  private def readMessageUnsafe(path: String): String = {
    val bufferedSource = Source.fromFile(path)
    val result = bufferedSource.getLines.mkString
    bufferedSource.close
    result
  }
}

package com.eg.plugin.util

import java.io.{FileNotFoundException, IOException, PrintWriter}
import java.lang.System.lineSeparator

import cats.implicits._
import com.intellij.openapi.vfs.VirtualFile

import scala.io.Source

object SbtHelper extends TryWithResources {

  private val sbtFileName = "build.sbt"

  def changeSbtSetting(
    rootFolder: VirtualFile,
    initialValue: String,
    replaceValue: String
  ): Either[IOException, Unit] = {
    rootFolder.getFileSystem.refresh(false)
    Option(rootFolder.findChild(sbtFileName)) match {
      case Some(sbtFile) =>
        withResources(
          new PrintWriter(sbtFile.getOutputStream(None))
        )(writer => {
          val newConfig = Source
            .fromInputStream(sbtFile.getInputStream)
            .getLines()
            .mkString(lineSeparator)
            .replaceAll(initialValue, replaceValue)
          writer.write(newConfig)
          }.asRight)
      case None          =>
        new FileNotFoundException(
          s"Unfortunately, the build.sbt file is missing."
        ).asLeft
    }
  }
}

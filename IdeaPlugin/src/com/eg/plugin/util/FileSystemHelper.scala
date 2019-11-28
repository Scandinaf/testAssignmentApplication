package com.eg.plugin.util

import java.io.{File, IOException}

import cats.implicits._
import com.intellij.openapi.vfs.{LocalFileSystem, VirtualFile}

object FileSystemHelper {

  def createUniqueDirectory(
    initDirectory: VirtualFile,
    folderName: String
  ): Either[IOException, VirtualFile] =
    if (initDirectory.exists() && initDirectory.isDirectory)
      initDirectory.createChildDirectory(
        None,
        getUniqueFolderName(initDirectory, folderName)
      ).asRight
    else
      new IOException(
        s"""An error occurred while trying to create directory.
           |Directory(exists - ${ initDirectory.exists() };
           |isDirectory - ${ initDirectory.isDirectory };
           |path - ${ initDirectory.getPath })""".stripMargin
      ).asLeft

  def clearDirectory(directory: VirtualFile, excludeFiles: Seq[String] = Seq.empty): Either[IOException, Unit] =
    (directory.exists(), directory.isDirectory) match {
      case (true, true) => directory.getChildren.view
        .filterNot(f => excludeFiles.exists(fileName => f.getName == fileName))
        .foreach(_.delete(None)).asRight
      case (v1, v2)     =>
        new IOException(
          s"""An error occurred while trying to clear directory.
             |Directory(exists - $v1; isDirectory - $v2; path - ${ directory.getPath })""".stripMargin
        ).asLeft
    }

  def copyDirectoryContent(
    directory: VirtualFile,
    destination: VirtualFile
  ): Either[IOException, Unit] =
    (directory.exists(), directory.isDirectory, destination.exists(), destination.isDirectory) match {
      case (true, true, true, true) => directory.getChildren
        .foreach(f =>
          FileSystemHelper.copyFile(f, f.getName, destination)
        ).asRight
      case (v1, v2, v3, v4)         =>
        new IOException(
          s"""An error occurred while trying to copy.
             |Directory(exists - $v1; isDirectory - $v2; path - ${ directory.getPath }),
             |Destination(exists - $v3; isDirectory - $v4; path - ${ destination.getPath })""".stripMargin
        ).asLeft
    }

  def getVirtualFile(path: String): Option[VirtualFile] =
    getFile(path).map(getVirtualFileByFile(_))

  def getVirtualFileFromResources(path: String): Option[VirtualFile] =
    getResourceFile(path).map(getVirtualFileByFile(_))

  private def copyFile(file: VirtualFile, newName: String, destination: VirtualFile): Unit =
    file.copy(None, destination, newName)

  private def getResourceFile(path: String): Option[File] =
    Option(getClass.getResource(path))
      .map(r => new File(r.getPath))

  private def getFile(path: String): Option[File] =
    new File(path).some.collect {
      case f if f.exists() => f
    }

  private def getVirtualFileByFile(file: File): VirtualFile =
    LocalFileSystem
      .getInstance()
      .findFileByIoFile(file)

  private def getUniqueFolderName(
    directory: VirtualFile,
    folderName: String,
    attempt: Option[Int] = None
  ): String = {
    val fullFolderName = s"$folderName${ buildPostfix(attempt) }"
    Option(directory.findChild(fullFolderName)) match {
      case Some(_) =>
        getUniqueFolderName(
          directory,
          folderName,
          (attempt.getOrElse(0) + 1).some
        )
      case None    => fullFolderName
    }
  }

  private def buildPostfix(attempt: Option[Int]): String =
    attempt match {
      case Some(value) => s"_$value"
      case None        => ""
    }
}

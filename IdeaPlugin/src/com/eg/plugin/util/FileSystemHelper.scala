package com.eg.plugin.util

import java.io.{File, IOException, InputStream}
import java.nio.file.{Path, Paths}

import cats.implicits._
import com.intellij.openapi.vfs.{VfsUtil, VirtualFile}

object FileSystemHelper extends TryWithResources {

  def createDirectory(
    initDirectory: VirtualFile,
    folderName: String
  ): Either[IOException, VirtualFile] =
    createDirectory(initDirectory, () => folderName)

  def createUniqueDirectory(
    initDirectory: VirtualFile,
    folderName: String
  ): Either[IOException, VirtualFile] = {
    initDirectory.getFileSystem.refresh(false)
    createDirectory(initDirectory, () => getUniqueFolderName(initDirectory, folderName))
  }

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
      case (true, true, true, true) =>
        val destinationPath = Paths.get(destination.getPath)
        directory.getChildren.foreach(copy(_, destinationPath)).asRight

      case (v1, v2, v3, v4) =>
        new IOException(
          s"""An error occurred while trying to copy.
             |Directory(exists - $v1; isDirectory - $v2; path - ${ directory.getPath }),
             |Destination(exists - $v3; isDirectory - $v4; path - ${ destination.getPath })""".stripMargin
        ).asLeft
    }

  def getVirtualFile(path: String): Option[VirtualFile] =
    getFile(path).map(getVirtualFileByFile(_))

  def getVirtualFileFromResources(path: String): Option[VirtualFile] =
    Option(getClass.getResource(path)).map(VfsUtil.findFileByURL(_))

  private def copy(file: VirtualFile, path: Path): Unit =
    if (file.isDirectory)
      copyDirectory(file, path)
    else
      copyFile(file, path)

  private def createDirectory(
    initDirectory: VirtualFile,
    getFolderName: () => String
  ): Either[IOException, VirtualFile] =
    if (initDirectory.exists() && initDirectory.isDirectory)
      initDirectory.createChildDirectory(
        None,
        getFolderName()
      ).asRight
    else
      new IOException(
        s"""An error occurred while trying to create directory.
           |Directory(exists - ${ initDirectory.exists() };
           |isDirectory - ${ initDirectory.isDirectory };
           |path - ${ initDirectory.getPath })""".stripMargin
      ).asLeft

  private def copyDirectory(initDirectory: VirtualFile, destination: Path): Unit = {
    val newDestination = destination.resolve(initDirectory.getName)
    new File(newDestination.toUri).mkdir()
    initDirectory.getChildren.foreach(copy(_, newDestination))
  }

  private def copyFile(initFile: VirtualFile, destination: Path): Unit = {
    val file = new File(destination.resolve(initFile.getName).toUri)
    file.createNewFile()
    withResources(initFile.getInputStream())(is => inputStreamToFile(is, file))
  }

  private def inputStreamToFile(is: InputStream, file: File): Unit =
    withResources(
      new java.io.FileOutputStream(file)
    )(t => t.write(Stream.continually(is.read()).takeWhile(_ != -1).map(_.toByte).toArray))

  private def getFile(path: String): Option[File] =
    new File(path).some.collect {
      case f if f.exists() => f
    }

  private def getVirtualFileByFile(file: File): VirtualFile =
    VfsUtil.findFileByIoFile(file, true)

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

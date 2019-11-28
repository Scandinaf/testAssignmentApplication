package com.eg.assignment.common.helper

import com.eg.assignment.common.exception.EnvVariableNotFoundException

import scala.util.Properties

object ENVVariableHelper {
  def getENVVariable(name: String): Either[EnvVariableNotFoundException, String] =
    Properties.envOrNone(name) match {
      case Some(envValue) => Right(envValue)
      case None           => Left(new EnvVariableNotFoundException(s"The Environment variable '$name' hasn't been set."))
    }

  def isENVVariableExist(name: String): Boolean =
    Properties.envOrNone(name).isDefined
}

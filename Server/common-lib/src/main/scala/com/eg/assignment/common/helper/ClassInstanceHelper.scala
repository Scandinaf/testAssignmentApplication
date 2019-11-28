package com.eg.assignment.common.helper

import scala.reflect.runtime.{universe => ru}
import scala.util.Try

object ClassInstanceHelper {
  private val rm = ru.runtimeMirror(getClass.getClassLoader)

  def newInstance[T](ancestor: String, args: Any*): Either[Exception, T] =
    for {
      cs <- getClassSymbol(ancestor)
      pConstructor = cs.primaryConstructor.asMethod
      instance <- invokeConstructor(cs, pConstructor)
    } yield instance.asInstanceOf[T]

  private def invokeConstructor(
    cs: ru.ClassSymbol,
    constructor: ru.MethodSymbol,
    args: Any*
  ): Either[IllegalArgumentException, Any] = {
    val constructorMethod = rm.reflectClass(cs).reflectConstructor(constructor)
    Try(constructorMethod.apply(args)).toEither.left.map(_ =>
      new IllegalArgumentException(s"It was impossible to create a class(${ cs.fullName }) using a constructor. Please check your implementation.")
    )
  }

  private def getClassSymbol(ancestor: String): Either[ClassNotFoundException, ru.ClassSymbol] =
    Try(rm.staticClass(ancestor))
      .toEither
      .left
      .map(_ => new ClassNotFoundException(s"Couldn't find the class - $ancestor."))
}

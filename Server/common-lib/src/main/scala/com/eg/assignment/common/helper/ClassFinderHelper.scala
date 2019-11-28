package com.eg.assignment.common.helper

import org.clapper.classutil.{ClassFinder, ClassInfo}

object ClassFinderHelper {
  private val finder = ClassFinder()

  def findSubclasses(ancestor: String): List[ClassInfo] =
    ClassFinder.concreteSubclasses(ancestor, finder.getClasses()).toList
}

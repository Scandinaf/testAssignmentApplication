package com.eg.assignment.checker.service.test

import com.eg.assignment._

case class ShortestPathTestCase(start: Vertex, end: Vertex, edges: Set[Edge]) {
  override def toString: String = s"Test case with ${edges.size} edges"
}

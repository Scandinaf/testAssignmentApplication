package com.eg.assignment

final case class Vertex(id: String) extends AnyVal

final case class Edge(from: Vertex, to: Vertex, cost: Double)

trait ShortestPath {
  def findPath(start: Vertex, end: Vertex, graph: Set[Edge]): List[Vertex]
}

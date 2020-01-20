package com.eg.assignment

object Graph {
  def allVertices(graph: Set[Edge]): Set[Vertex] = {
    graph.map(_.from) ++ graph.map(_.to)
  }

  def allVertexPairs(edges: Set[Edge]): Set[(Vertex, Vertex)] = {
    allVertexPairsFromVertices(allVertices(edges))
  }

  def allVertexPairsFromVertices(vertices: Set[Vertex]): Set[(Vertex, Vertex)] = {
    vertices flatMap { a =>
      vertices flatMap { b =>
        if (a == b) {
          None // no path to itself
        } else {
          Some((a, b))
        }
      }
    }
  }
}

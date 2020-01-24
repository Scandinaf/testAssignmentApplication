package com.eg.assignment

import scala.util.Random

object GraphGenerator {
  private def createVertices(vertexCount: Int): Set[Vertex] = {
    val result = (0 until vertexCount) map { x =>
      Vertex(s"vertex-$x")
    }
    result.toSet
  }

  private def allEdgePairs(vertexCount: Int): Set[(Vertex, Vertex)] = {
    val vertices = createVertices(vertexCount)
    Graph.allVertexPairsFromVertices(vertices)
  }

  /** https://en.wikipedia.org/wiki/Erdős–Rényi_model */
  def generatePartiallyConnectedDigraph(vertexCount: Int, connectionProbability: Double, seed: Long): Set[Edge] = {
    val random = new Random(seed)

    val results = allEdgePairs(vertexCount) flatMap { case (a, b) =>
      if (random.nextDouble() < connectionProbability) {
        Some(Edge(a, b, random.nextDouble()))
      } else {
        None
      }
    }

    results
  }

  def generateCompleteDigraph(vertexCount: Int, seed: Long): Set[Edge] = {
    val random = new Random(seed)

    val results = allEdgePairs(vertexCount) map { case (a, b) =>
      Edge(a, b, random.nextDouble())
    }

    results
  }
}

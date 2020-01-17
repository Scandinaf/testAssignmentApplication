package com.eg.assignment

import scala.collection.mutable

object FloydWarshall {
  /** https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm */
  def findCosts(graph: Set[Edge]): Map[(Vertex, Vertex), Double] = {
    val map = mutable.Map[(Vertex, Vertex), Double]()

    allVertexPairs(graph) foreach { x =>
      map(x) = Double.PositiveInfinity // let dist be a |V| × |V| array of minimum distances initialized to ∞ (infinity)
    }

    graph foreach { edge => // for each edge (u,v)
      map((edge.from, edge.to)) = edge.cost // dist[u][v] ← w(u,v)  // the weight of the edge (u,v)
    }

    val vertices = allVertices(graph)
    vertices foreach { vertex => // for each vertex v
      map((vertex, vertex)) = 0 // dist[v][v] ← 0
    }

    vertices foreach { k => // for k from 1 to |V|
      vertices foreach { i => // for i from 1 to |V|
        vertices foreach { j => // for j from 1 to |V|
          val tmp = map((i, k)) + map((k, j))
          if (map((i, j)) > tmp) { // if dist[i][j] > dist[i][k] + dist[k][j]
            map((i, j)) = tmp // dist[i][j] ← dist[i][k] + dist[k][j]
          }
        }
      }
    }
    map.toMap
  }
}

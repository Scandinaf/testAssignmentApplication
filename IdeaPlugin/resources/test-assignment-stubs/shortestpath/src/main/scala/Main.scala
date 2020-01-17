import cats.implicits._
import com.eg.assignment._

class ShortestPathSolution extends ShortestPath {
  /** Find the shortest path from start to end vertices.
    *
    * @param start Start vertex (start of the path)
    * @param end End vertex (end of the path)
    * @param graph Edges of the graph
    *
    * @return The list of vertices which connect start and end vertices and go
    * along valid edges of the graph, with the least total "cost". You can
    * assume such a path will always exist.
    */
  override def findPath(start: Vertex, end: Vertex, graph: Set[Edge]): List[Vertex] = {
    // TODO: adjust or fully rewrite this code to achieve a more optimal path.
    // The path returned by this solution is (on purpose) highly sub-optimal
    // and it may not work for all test cases.

    def fullPath(path: List[Vertex]): Boolean =
      (path.head === end) && (path.last === start)

    def visit(remainingEdges: Set[Edge], visited: List[Vertex]): Option[List[Vertex]] = {
      if (fullPath(visited)) {
        Some(visited) // we have found a path
      } else {
        visited.headOption.flatMap { current =>
          val outgoingEdges = remainingEdges filter (_.from === current)
          val solutions = outgoingEdges.view map { candidate =>
            visit(remainingEdges - candidate, candidate.to :: visited)
          }
          solutions collectFirst {
            case Some(solution) => solution
          }
        }
      }
    }

    val result = visit(graph, start :: Nil)

    result.map(_.reverse) getOrElse {
      sys.error("Path could not be found")
    }
  }
}

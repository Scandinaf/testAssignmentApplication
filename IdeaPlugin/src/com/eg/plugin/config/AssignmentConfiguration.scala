package com.eg.plugin.config

import cats.data.NonEmptyList
import com.eg.plugin.model.{AssignmentDescription, AssignmentConfiguration => Configuration}

object AssignmentConfiguration {
  val assignmentsConfiguration: NonEmptyList[Configuration] = NonEmptyList.of(
    Configuration(
      description = AssignmentDescription(
        "calculator",
        "Calculator",
        "Simple task to implement the calculator"
      ),
      sbtCommand = ";project root;reload;compile;assembly"
    ),
    Configuration(
      description = AssignmentDescription(
        "shortestpath",
        "Shortest path",
        """
          |Find a path in the provided graph which visits all vertices (and returns to the initial vertex, thus it is circular).
          |
          |You solution should attempt to develop a solution which calculates as short a path as possible (even if not necessarily the shortest possible), given the travel times as input.
          |
          |Assume the following classes are provided:
          |
          |package com.evolutiongaming.conferences.graph
          |
          |final case class Vertex(id: String) extends AnyVal
          |final case class Edge(from: Vertex, to: Vertex, cost: Double)
          |
          |The path will always exist in the test cases that your solution will be invoked upon.
          |
          |The travel possibilities are expressed as a set of Edge-s, and the shortest path should be returned as a List[Vertex]. The first and last vertices should be identical in the resulting List.
          |
          |The solution will be graded on test cases with vertex counts up to 100 and edge counts up to 9,900 and the total execution time of 10 test cases of various size constrained to 100 seconds.
      """.stripMargin
      ),
      sbtCommand = ";project root;reload;compile;scalafix;assembly"
    )
  )

  val assignmentsDescription: Array[AssignmentDescription] =
    assignmentsConfiguration.map(_.description).toList.toArray

  def getProjectPath(projectName: String) =
    s"${PluginConfiguration.pathToStub}/$projectName"
}

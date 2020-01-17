package com.eg.assignment.checker.service.test

import cats.implicits._
import com.eg.assignment._
import com.eg.assignment.common.model.result.{TestResult, TestSuiteResult}

class ShortestPathTestSuite(val instance: ShortestPath) extends TestSuite[ShortestPath] {
  private val completeGraphTestCases: List[ShortestPathTestCase] = {
    val graphs = List(5, 10, 20, 100, 250, 500) map { vertexCount =>
      GraphGenerator.generateCompleteDigraph(vertexCount, 0)
    }

    graphs map { graph =>
      val max = graph.maxBy(_.cost)
      ShortestPathTestCase(max.from, max.to, graph)
    }
  }

  private def generateIncompleteGraphTestCase(vertexCount: Int, connectionProbability: Double, seed: Long): ShortestPathTestCase = {
    val graph = GraphGenerator.generatePartiallyConnectedDigraph(vertexCount, connectionProbability, seed)
    val modifiedGraph = graph.map(_.copy(cost = 1)) // set all edges to 1 to find "interesting" paths with lots of vertices travelled

    val distancesIgnoringCost = FloydWarshall.findCosts(modifiedGraph)

    val ((a, b), _) = distancesIgnoringCost maxBy { case (_, cost) =>
      if (cost.isPosInfinity) 0 else cost
    }

    ShortestPathTestCase(a, b, graph)
  }

  private val incompleteGraphTestCases: List[ShortestPathTestCase] = {
    List(20, 100) flatMap { vertexCount =>
      List(0.1d, 0.25d) map { connectionProbability =>
        generateIncompleteGraphTestCase(vertexCount, connectionProbability, 0L)
      }
    }
  }

  private def gradeOne(solution: ShortestPath, testCase: ShortestPathTestCase): Either[String, Double] = {
    val result = solution.findPath(testCase.start, testCase.end, testCase.edges)

    if (result.head != testCase.start) {
      Left(s"Head ${result.head} is not start ${testCase.start}")
    } else if (result.last != testCase.end) {
      Left(s"Last ${result.last} is not end ${testCase.end}")
    } else {
      val pairs = result.init zip result.tail

      val results = pairs traverse { case (a, b) =>
        val edgeCost = testCase.edges.find(edge => edge.from == a && edge.to == b).map(_.cost)
        Either.fromOption(edgeCost, s"Failed to find edge from $a to $b in graph")
      }

      results.map(_.sum / testCase.edges.map(_.from).size)
    }
  }

  private val testCases = incompleteGraphTestCases ::: completeGraphTestCases

  def runTestSuite: TestSuiteResult = {
    val results = testCases.map(gradeOne(instance, _))
    def intScore(s: Double) = (s * 1000).toInt
    TestSuiteResult(
      name = "ShortestPathTestSuite",
      isPassed = results.forall(_.isRight),
      resultScore = results.sequence.map(_.sum).map(intScore).toOption,
      hint = None,
      testResults = results.map { r =>
        TestResult(
          isPassed = r.isRight,
          resultScore = r.map(intScore).toOption,
          hint = r.swap.toOption,
        )
      },
    )
  }
}

object ShortestPathTestSuite {
  def apply(instance: ShortestPath): ShortestPathTestSuite = new ShortestPathTestSuite(instance)
}

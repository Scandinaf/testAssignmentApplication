package com.eg.plugin.action.ui

import java.awt.{BorderLayout, Component}
import cats.syntax.option._
import com.eg.assignment.common.model.result.{AssignmentCheckResult, TestResult, TestSuiteResult}
import com.eg.plugin.action.ui.AssignmentCheckResultDialog.{Icons, Message, Size}
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.Borders
import javax.swing._
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import javax.swing.tree._

class AssignmentCheckResultDialog(
                                   assignmentCheckResult: AssignmentCheckResult,
                                   project: Project
                                 ) extends DialogWrapper(project, false)
  with UiComponent {
  init()

  override def init: Unit = {
    init(Message.title)
    super.init()
  }

  override def createCenterPanel: JComponent = {
    val rootPanel = new JPanel(new BorderLayout(JBUIScale.scale(5), 0))
    rootPanel.setPreferredSize(JBUI.size(Size.Window.width, Size.Window.height))
    rootPanel.add(
      createAdditionalInformationPanel(assignmentCheckResult),
      "North"
    )
    rootPanel.add(createTestResultsPanel, "Center")
    rootPanel
  }

  protected def createAdditionalInformationPanel(
                                                  assignmentCheckResult: AssignmentCheckResult
                                                ): JPanel = {
    val additionalInformationPanel = new JPanel(new BorderLayout(JBUIScale.scale(5), 0))
    additionalInformationPanel.setBorder(Borders.empty(0, 0, 5, 0))
    additionalInformationPanel.add(
      createScrollPane(createTextArea(
        buildText(
          resultScore = assignmentCheckResult.resultScore,
          additionalInformation = assignmentCheckResult.additionalInformation
        ).some
      ),
        Size.AdditionalInformationTextArea.width,
        Size.AdditionalInformationTextArea.height),
      "North"
    )
    additionalInformationPanel
  }

  protected def createTestResultsPanel: JPanel = {
    val testResultsPanel = new JPanel(new BorderLayout(JBUIScale.scale(5), 0))
    val tree = createTree(buildRootNode, buildTreeCellRenderer)
    val textArea = createTextArea()
    tree.getSelectionModel.addTreeSelectionListener(createTreeSelectionListener(tree, textArea))
    testResultsPanel.add(createScrollPane(
      createTreeSpeedSearch(tree).getComponent, Size.Tree.width, Size.Tree.height),
      "West"
    )
    testResultsPanel.add(
      createScrollPane(textArea, Size.Tree.width, Size.Tree.height),
      "Center"
    )
    testResultsPanel
  }

  private def buildRootNode: TreeNode = {
    val root: DefaultMutableTreeNode = new DefaultMutableTreeNode(assignmentCheckResult)
    assignmentCheckResult.testSuiteResults.foreach(testSuite => {
      val testSuiteNode = new DefaultMutableTreeNode(testSuite)
      testSuite.testResults.foreach(test => {
        val testNode = new DefaultMutableTreeNode(test)
        testSuiteNode.add(testNode)
      })
      root.add(testSuiteNode)
    })
    root
  }

  private def createTreeSelectionListener(tree: JTree, textArea: JTextArea): TreeSelectionListener =
    (_: TreeSelectionEvent) => {
      tree.getLastSelectedPathComponent.asInstanceOf[DefaultMutableTreeNode].getUserObject match {
        case v: TestResult =>
          textArea.setText(buildText(v.hint, v.resultScore, v.executionTime))
        case v: TestSuiteResult =>
          textArea.setText(buildText(v.hint, v.resultScore))
        case _: AssignmentCheckResult =>
          textArea.setText("")
      }
    }

  private def buildTreeCellRenderer: TreeCellRenderer =
    new DefaultTreeCellRenderer() {
      override def getTreeCellRendererComponent(
                                                 tree: JTree,
                                                 value: Any,
                                                 sel: Boolean,
                                                 expanded: Boolean,
                                                 leaf: Boolean,
                                                 row: Int,
                                                 hasFocus: Boolean
                                               ): Component = {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        value.asInstanceOf[DefaultMutableTreeNode].getUserObject match {
          case v: TestResult =>
            setText(v.name.getOrElse("Regular Test"))
            buildIcon(v.isPassed).foreach(setIcon(_))
          case v: TestSuiteResult =>
            setText(v.name)
            buildIcon(v.isPassed).foreach(setIcon(_))
          case v: AssignmentCheckResult =>
            setText("Test Suites")
            buildIcon(v.isPassed).foreach(setIcon(_))
        }
        this
      }
    }

  private def buildIcon(
                         isPassed: Boolean
                       ): Option[ImageIcon] =
    if (isPassed) Icons.ok
    else Icons.fail

  private def buildText(
                         hint: Option[String] = None,
                         resultScore: Option[Int] = None,
                         executionTime: Option[Long] = None,
                         additionalInformation: Option[String] = None
                       ): String =
    s"""Hint: ${
      hint.getOrElse(
        "Unfortunately, we don't know how to help you, but we think you're already doing a great job.:)"
      )
    }
       |
       |Score: ${
      resultScore.getOrElse(
        "We couldn't calculate the number of points you scored, apparently we don't need them to assess your coolness. :)"
      )
    }
       |
       |Execution Time(ms): ${
      executionTime.getOrElse(
        "Unfortunately, our stopwatch broke down, but someday we'll definitely fix it. :)"
      )
    }
       |
       |Additional Information: ${
      additionalInformation.getOrElse(
        "Unfortunately, it's empty. :)"
      )
    }""".stripMargin
}

object AssignmentCheckResultDialog {
  def apply(result: AssignmentCheckResult, project: Project): AssignmentCheckResultDialog =
    new AssignmentCheckResultDialog(result, project)

  object Size {

    object Window {
      val width = 1000
      val height = 400
    }

    object AdditionalInformationTextArea {
      val width = 100
      val height = 200
    }

    object Tree {
      val width = 300
      val height = 400
    }

  }

  object Message {
    val title = "Test Assignment Results"
  }

  object Icons {
    val ok: Option[ImageIcon] =
      Option(new ImageIcon(getClass.getResource("/icons/checkmark.png")))
    val fail: Option[ImageIcon] =
      Option(new ImageIcon(getClass.getResource("/icons/delete.png")))
  }

}

package com.eg.plugin.action.ui

import java.awt.Component

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.wm.impl.welcomeScreen.FlatWelcomeFrame
import com.intellij.ui.components.JBList
import com.intellij.ui.{ListSpeedSearch, TreeSpeedSearch}
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.Borders
import javax.swing._
import javax.swing.tree.{TreeCellRenderer, TreeNode}

trait UiComponent {
  self: DialogWrapper =>

  protected def init(title: String): Unit = {
    setResizable(false)
    setModal(true)
    setTitle(title)
  }

  protected def createTextArea(
                                text: Option[String] = None
                              ): JTextArea = {
    val textArea = new JTextArea(5, 0)
    textArea.setBorder(Borders.empty(5))
    textArea.setEnabled(false)
    textArea.setLineWrap(true)
    textArea.setWrapStyleWord(true)
    text.map(textArea.setText(_))
    textArea
  }

  protected def createScrollPane(
                                  component: Component,
                                  width: Int,
                                  height: Int
                                ): JScrollPane = {
    val scrollPane = new JScrollPane(component)
    scrollPane.setBackground(FlatWelcomeFrame.getProjectsBackground())
    val size = JBUI.size(width, height)
    scrollPane.setSize(size)
    scrollPane.setMinimumSize(size)
    scrollPane.setPreferredSize(size)
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    scrollPane.setHorizontalScrollBarPolicy(
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    scrollPane
  }

  protected def createTreeSpeedSearch(
                                       tree: JTree
                                     ): TreeSpeedSearch =
    new TreeSpeedSearch(tree)

  protected def createListSpeedSearch[E](
                                          list: JBList[E]
                                        ): ListSpeedSearch[E] =
    new ListSpeedSearch(list)

  protected def createTree(rootNode: TreeNode, cellRenderer: TreeCellRenderer): JTree = {
    val tree = new JTree(rootNode)
    tree.setCellRenderer(cellRenderer)
    tree.setShowsRootHandles(true)
    tree.setRootVisible(true)
    tree
  }

  protected def createLabel(text: String, width: Int, height: Int): JLabel = {
    val label = new JLabel(text)
    label.setHorizontalTextPosition(SwingConstants.LEFT)
    label.setSize(width, height)
    label
  }

  protected def createTextField(width: Int, height: Int, columns: Int): JTextField = {
    val textField = new JTextField(columns)
    textField.setSize(width, height)
    textField
  }
}

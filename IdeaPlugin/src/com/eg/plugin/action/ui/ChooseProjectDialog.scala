package com.eg.plugin.action.ui

import java.awt.BorderLayout

import cats.implicits._
import com.eg.plugin.action.ui.ChooseProjectDialog._
import com.eg.plugin.action.ui.model.ListEntity
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBList
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import javax.swing._
import javax.swing.border.EmptyBorder
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}

class ChooseProjectDialog[E <: ListEntity](data: Array[E])
  extends DialogWrapper(false)
    with UiComponent {
  protected val list = buildList
  init()

  override def init: Unit = {
    init(Message.title)
    super.init()
  }

  override def createCenterPanel: JComponent = {
    val rootPanel = new JPanel(new BorderLayout(JBUIScale.scale(5), 0))
    rootPanel.setPreferredSize(JBUI.size(Size.Window.width, Size.Window.height))
    val textArea = createTextArea(list.getSelectedValue.getHint.some)
    list.addListSelectionListener(createListSelectionListener(textArea))

    rootPanel.add(createScrollPane(
      createListSpeedSearch(list).getComponent,
      Size.JList.width,
      Size.JList.height
    ), "West")
    rootPanel.add(createScrollPane(
      textArea,
      Size.TextArea.width,
      Size.TextArea.height
    ), "Center")

    rootPanel
  }

  def getSelectedValue: E = list.getSelectedValue

  protected def createListSelectionListener(textArea: JTextArea): ListSelectionListener =
    (_: ListSelectionEvent) => {
      textArea.setText(list.getSelectedValue.getHint)
    }

  protected def buildList: JBList[E] = {
    import Size.Cell._

    val list = new JBList[E]()
    list.setListData(data)
    list.setCellRenderer(buildListCellRenderer)
    list.setSelectedIndex(0)
    list.setFixedCellHeight(height)
    list.setFixedCellWidth(width)
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    list
  }

  protected def buildListCellRenderer: ListCellRenderer[E] =
    new SimpleListCellRenderer[E] {
      override def customize(
        jList: JList[_ <: E],
        t: E,
        i: Int,
        b: Boolean,
        b1: Boolean): Unit = {
        setBorder(Size.Cell.border)
        setText(t.getText)
      }
    }
}

object ChooseProjectDialog {
  object Size {
    object Window {
      val width = 500
      val height = 300
    }

    object Cell {
      val width = 250
      val height = 50
      val border = new EmptyBorder(0, 10, 0, 10)
    }

    object TextArea {
      val width = 500
      val height = 400
    }

    object JList {
      val width = 300
      val height = 400
    }
  }

  object Message {
    val title = "Import Project"
  }

  def apply[E <: ListEntity](data: Array[E]): ChooseProjectDialog[E] =
    new ChooseProjectDialog[E](data)
}

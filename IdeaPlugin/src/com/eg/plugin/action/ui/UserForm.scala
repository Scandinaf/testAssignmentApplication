package com.eg.plugin.action.ui

import java.awt.FlowLayout

import com.eg.assignment.common.model.assignment.UserInformation
import com.eg.plugin.action.ui.UserForm.Message
import com.eg.plugin.action.ui.validator.UserFormValidator
import com.intellij.openapi.ui.DialogWrapper
import javax.swing._

class UserForm extends DialogWrapper(false)
  with UserFormValidator
  with UiComponent {
  protected lazy val nicknameTextField = createTextField(
    UserForm.Size.TextField.width,
    UserForm.Size.TextField.height,
    UserForm.Size.TextField.columns
  )
  protected lazy val mailboxTextField = createTextField(
    UserForm.Size.TextField.width,
    UserForm.Size.TextField.height,
    UserForm.Size.TextField.columns
  )
  init()

  override def init: Unit = {
    import UserForm.Size.Window._
    init(Message.title)
    setSize(width, height)
    super.init()
  }

  override def createCenterPanel: JComponent =
    buildPanel

  def getUserInformation =
    UserInformation(nicknameTextField.getText, mailboxTextField.getText)

  protected def buildPanel: JPanel = {
    val panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.add(createPanel("Nickname:", nicknameTextField))
    panel.add(createPanel("Mailbox:   ", mailboxTextField))
    panel
  }

  protected def createPanel(labelText: String, field: JTextField): JPanel = {
    val panel = new JPanel()
    panel.setLayout(new FlowLayout(FlowLayout.LEFT))
    panel.add(createLabel(
      labelText,
      UserForm.Size.Label.width,
      UserForm.Size.Label.height
    ))
    panel.add(field)
    panel
  }
}

object UserForm {
  object Size {
    object Window {
      val width = 300
      val height = 150
    }

    object TextField {
      val width = 160
      val height = 25
      val columns = 20
    }

    object Label {
      val width = 50
      val height = 25
    }
  }

  object Message {
    val title = "User Information"
  }

  def apply(): UserForm = new UserForm()
}

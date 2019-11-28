package com.eg.plugin.action.ui.validator

import java.util

import cats.implicits._
import com.eg.plugin.action.ui.validator.UserFormValidator.Message._
import com.intellij.openapi.ui.{DialogWrapper, ValidationInfo}
import javax.swing.JTextField

import scala.collection.JavaConverters._

trait UserFormValidator {
  self: DialogWrapper =>
  protected val nicknameTextField: JTextField
  protected val mailboxTextField: JTextField

  override def doValidateAll(): util.List[ValidationInfo] =
    List(validateNickname, validateMailbox).flatten.asJava


  protected def validateNickname: Option[ValidationInfo] =
    nicknameTextField.getText.trim match {
      case "" => new ValidationInfo(nicknameEmpty, nicknameTextField).some
      case _  => None
    }

  protected def validateMailbox: Option[ValidationInfo] =
    mailboxTextField.getText.trim match {
      case email if email.isEmpty || isValid(email) => None
      case _                                        =>
        new ValidationInfo(mailboxInvalid, mailboxTextField).some
    }

  protected def isValid(email: String): Boolean =
    """(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email) != None

}

object UserFormValidator {
  object Message {
    val nicknameEmpty = "The nickname can't be empty"
    val mailboxInvalid = "There's an invalid mailbox"
  }
}

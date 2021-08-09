package frontend.operations

import backend.engine.{CompositeDB, CompositeOperation, BaseOperation, Operations}
import javafx.collections.ObservableList
import javafx.stage.Stage
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType, ListView, TextField}
import scalafxml.core.{DependenciesByType, FXMLLoader}
import scalafxml.core.macros.sfxml

import scala.annotation.tailrec

trait OperationsControllerInterface {
  def addOperation(): Unit
  def removeOperation(): Unit
  def clearList(): Unit
  def done(): Unit
}

@sfxml
class OperationsController(listOfBasics: ListView[String], listOfComposites: ListView[String], nameTextField: TextField,
                           listOfOperations: ListView[String], doneButton: Button) extends OperationsControllerInterface {
  // todo grey, inverter etc?
  val basics: ObservableList[String] = new ObservableBuffer[String]()
  basics.addAll("add", "sub", "inv sub", "mul", "div", "inv div")


  val composites: ObservableList[String] = new ObservableBuffer[String]()
  CompositeDB.composites.map(comp => composites.add(comp.toString))

  listOfBasics.setItems(basics)
  listOfComposites.setItems(composites)

  override def addOperation(): Unit = {
    def getSelected(list: ListView[String]): String = if (list.getSelectionModel.getSelectedItems.size() > 0) list.getSelectionModel.getSelectedItem else ""
    def foldSelected(list: List[ListView[String]]): String = list.foldLeft("")((s, ls) => s + getSelected(ls))
    def deselect(list: ListView[String]): Unit = list.getSelectionModel.clearSelection()
    def deselectAll(list: List[ListView[String]]): Unit = list.foreach(l => deselect(l))

    val all: List[ListView[String]] = listOfBasics::listOfComposites::Nil
    listOfOperations.getItems.add(foldSelected(all))
    deselectAll(all)
  }

  override def removeOperation(): Unit = {
    listOfOperations.getItems.remove(listOfOperations.getSelectionModel.getSelectedItem)
  }

  override def clearList(): Unit = listOfOperations.getItems.clear()

  def close(): Unit = {
    val stage: javafx.stage.Stage = listOfOperations.getScene.getWindow.asInstanceOf[Stage]
    stage.close()
  }

  def nameEmptyAlert(): Boolean = {
    val alert = new Alert(AlertType.Warning) {
      initOwner(listOfOperations.getScene.getWindow.asInstanceOf[Stage])
      title = "Confirmation Dialog"
      headerText = "The Name of the composite function has been left empty. Continuing will NOT save the composite function!"
      contentText = "Are you sure you want to continue?"
    }

    alertResult(alert)
  }

  def compositeListEmptyAlert(name: String): Boolean = {
    val alert = new Alert(AlertType.Warning) {
      initOwner(listOfOperations.getScene.getWindow.asInstanceOf[Stage])
      title = "Confirmation Dialog"
      headerText = s"The $name composite function is empty. Continuing will NOT save the composite function!"
      contentText = "Are you sure you want to continue?"
    }

    alertResult(alert)
  }

  private def alertResult(alert: Alert) = alert.showAndWait() match {
    case Some(ButtonType.OK) => true
    case _ => false
  }

  @tailrec
  private def getOperations(acc: List[Double => BaseOperation], nameList: List[String]): List[Double => BaseOperation] = nameList match {
    case Nil => acc
    case x::xs => getOperations(Operations.call(x)::acc, nameList.tail)
  }

  override def done(): Unit = {
    val name = nameTextField.getText
    if (name.isEmpty && nameEmptyAlert()) close()
    if (name.nonEmpty && listOfOperations.getItems.isEmpty && compositeListEmptyAlert(name)) close()
    if (name.nonEmpty && !listOfOperations.getItems.isEmpty) {
      val strList = listOfOperations.getItems.toArray.map(s => s.toString).toList
      val opList = getOperations(Nil, strList)
      println(opList)
      println(s"Save composite $name(${listOfOperations.getItems.toString})")
      close()
    }
  }

}

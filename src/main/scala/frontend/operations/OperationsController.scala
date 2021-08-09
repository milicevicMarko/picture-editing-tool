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
                           listOfOperations: ListView[String], functionArgument: TextField, doneButton: Button) extends OperationsControllerInterface {
  // todo grey, inverter etc?
  val basics: ObservableList[String] = new ObservableBuffer[String]()
  basics.addAll("add", "sub", "inv sub", "mul", "div", "inv div")


  val composites: ObservableList[String] = new ObservableBuffer[String]()
  CompositeDB.composites.map(comp => composites.add(comp.name))

  listOfBasics.setItems(basics)
  listOfComposites.setItems(composites)

  override def addOperation(): Unit = {
    def getSelected(arg: String, list: ListView[String]): String = if (list.getSelectionModel.getSelectedItems.size() > 0) s"${list.getSelectionModel.getSelectedItem}($arg)"  else ""
    def foldSelected(arg: String, list: List[ListView[String]]): String = list.foldLeft("")((s, ls) => s + getSelected(arg, ls))
    def deselect(list: ListView[String]): Unit = list.getSelectionModel.clearSelection()
    def deselectAll(list: List[ListView[String]]): Unit = list.foreach(l => deselect(l))

    if (functionArgument.getText.nonEmpty) {
      val arg = functionArgument.getText
      functionArgument.clear()
      val all: List[ListView[String]] = listOfBasics::listOfComposites::Nil
      listOfOperations.getItems.add(foldSelected(arg, all))
      deselectAll(all)
    }
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
  private def getOperations(acc: List[BaseOperation], nameList: List[String]): List[BaseOperation] = nameList match {
    case Nil => acc
    case x::xs =>
      val funName: String = x.split('(')(0)
      val funArg: Double = x.split('(')(1).split(')')(0).toDouble
      val function: BaseOperation = Operations.call(funName)(funArg)
      getOperations(function::acc, nameList.tail)
  }

  override def done(): Unit = {
    val name = nameTextField.getText
    if (name.isEmpty && nameEmptyAlert()) close()
    if (name.nonEmpty && listOfOperations.getItems.isEmpty && compositeListEmptyAlert(name)) close()
    if (name.nonEmpty && !listOfOperations.getItems.isEmpty) {
      val strList = listOfOperations.getItems.toArray.map(s => s.toString).toList
      val operations = getOperations(Nil, strList)
      Operations.createComposite(name, operations)
      close()
    }
  }

}

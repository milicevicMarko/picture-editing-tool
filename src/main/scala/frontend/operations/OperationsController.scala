package frontend.operations

import backend.engine.{BaseOperation, CompositeOperation, OperationManager, Operations}
import javafx.collections.ObservableList
import javafx.stage.Stage
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, ListView, TextField, TitledPane}
import scalafxml.core.macros.sfxml

import scala.annotation.tailrec

trait OperationsControllerInterface {
  def addOperation(): Unit
  def removeOperation(): Unit
  def clearList(): Unit
  def done(): Unit
}

@sfxml
class OperationsController(listOfBasics: ListView[String], listOfComposites: ListView[String], listOfFunctions: ListView[String],
                           nameTextField: TextField, functionArgument: TextField, listOfOperations: ListView[String],
                           titledPaneBasic: TitledPane, titledPaneFunctions: TitledPane, titledPaneComplex: TitledPane) extends OperationsControllerInterface {

  titledPaneBasic.expandedProperty().addListener((_,_,_) => deselectAll())
  titledPaneFunctions.expandedProperty().addListener((_,_,_) => deselectAll())
  titledPaneComplex.expandedProperty().addListener((_,_,_) => deselectAll())

  val basics: ObservableList[String] = new ObservableBuffer[String]()
  basics.addAll("add", "sub", "inv sub", "mul", "div", "inv div")

  val composites: ObservableList[String] = new ObservableBuffer[String]()
  OperationManager.composites.map(comp => composites.add(comp.name))

  val functions: ObservableList[String] = new ObservableBuffer[String]()
  functions.addAll("pow", "log", "abs", "min", "max", "greyscale", "invert") // , "median", "ponder")

  listOfBasics.setItems(basics)
  listOfComposites.setItems(composites)
  listOfFunctions.setItems(functions)

  val allLists: List[ListView[String]] = listOfBasics::listOfFunctions::listOfComposites::Nil

  def deselectAll(): Unit = allLists.foreach(list => list.getSelectionModel.clearSelection())

  override def addOperation(): Unit = {
    def tryToGetSelected(arg: String, list: ListView[String]): String = if (list.getSelectionModel.getSelectedItems.size() > 0) s"${list.getSelectionModel.getSelectedItem}($arg)"  else ""
    def getSelected(arg: String, list: List[ListView[String]]): String = list.foldLeft("")((s, ls) => s + tryToGetSelected(arg, ls))

    if (functionArgument.getText.nonEmpty) {
      val arg = functionArgument.getText
      functionArgument.clear()
      listOfOperations.getItems.add(getSelected(arg, allLists))
      deselectAll()
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
      getOperations(function::acc, xs)
  }

  override def done(): Unit = {
    val name = nameTextField.getText
    if (name.isEmpty && nameEmptyAlert()) close()
    if (name.nonEmpty && listOfOperations.getItems.isEmpty && compositeListEmptyAlert(name)) close()
    if (name.nonEmpty && !listOfOperations.getItems.isEmpty) {
      val strList = listOfOperations.getItems.toArray.map(s => s.toString).toList
      val operations = getOperations(Nil, strList).reverse
      OperationManager.composites.addOne(new CompositeOperation(name, operations))
      close()
    }
  }

}

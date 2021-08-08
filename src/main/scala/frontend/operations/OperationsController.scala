package frontend.operations

import backend.engine.{CompositeDB, CompositeOperation, Operations}
import javafx.collections.ObservableList
import javafx.stage.Stage
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ListView, TextField}
import scalafxml.core.{DependenciesByType, FXMLLoader}
import scalafxml.core.macros.sfxml

trait OperationsControllerInterface {
  def addOperation(): Unit
  def removeOperation(): Unit
  def clearList(): Unit
  def done(): Unit
}

@sfxml
class OperationsController(listOfBasics: ListView[String], listOfComposites: ListView[String], nameTextField: TextField,
                           listOfOperations: ListView[String], doneButton: Button) extends OperationsControllerInterface {
//("add", "sub", "inv sub", "mul", "div", "inv div") grey, inverter etc?
  val basics: ObservableList[String] = new ObservableBuffer[String]()
  val tempBasics = List("add", "sub", "inv sub", "mul", "div", "inv div")
  tempBasics.foreach(op => basics.add(op))

  val composites: ObservableList[String] = new ObservableBuffer[String]()
  CompositeDB.composites.map(comp => composites.add(comp.toString))

  listOfBasics.setItems(basics)
  listOfComposites.setItems(composites)

  override def addOperation(): Unit = println(listOfBasics.getSelectionModel.getSelectedItem)

  override def removeOperation(): Unit = println(listOfBasics.getSelectionModel.getSelectedItem)

  override def clearList(): Unit = println("Empty")

  override def done(): Unit = {
    val name = nameTextField.getText
    listOfOperations.getItems.forEach(i => println(i))
    val stage: javafx.stage.Stage = listOfOperations.getScene.getWindow.asInstanceOf[Stage]
    stage.close()
  }

}

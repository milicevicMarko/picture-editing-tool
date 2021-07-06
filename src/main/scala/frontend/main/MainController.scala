package frontend.main

import backend.io.FileBrowser
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL
import java.io.File

trait MainInterface {
  def open(): Unit
  def save(): Unit
  def saveAs(): Unit
  def close(): Unit
  def setStage(stage: Stage)
}

@sfxml
class MainController(selectBtn: Button, cropBtn: Button, footerLb: Label)
  extends MainInterface {
  var stage: Option[Stage] = None

  override def setStage(stage: Stage): Unit = this.stage = Some(stage)

  override def open(): Unit = FileBrowser.importFile(stage) match {
    case f: File => println(f.toString)
    case _ => println("Canceled")
  }

  override def save(): Unit = ???

  override def saveAs(): Unit = ???

  override def close(): Unit = Platform.exit()

}

object MainControllerApp extends JFXApp3 {
  override def start(): Unit = {
    val url: URL = getClass.getResource("resources/main.fxml")
    val loader = new FXMLLoader(url, new DependenciesByType(Map()))

    loader.load()

    val root: Parent = loader.getRoot[jfxs.Parent]

    val controller: MainInterface = loader.getController[MainInterface]

    stage = new JFXApp3.PrimaryStage() {
      title = "FPhotoshop"
      scene = new Scene(root)
    }
    controller.setStage(stage)
  }
}

package frontend.main

import backend.engine.Engine
import backend.io.{FileBrowser, FileExport, FileImport}
import frontend.exit.ExitController
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.stage.{Stage, WindowEvent}
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL
import java.io.File

trait MainInterface {
  def open(): Unit
  def save(): Unit
  def saveAs(): Unit
  def close(): Unit
  def testMe(): Unit
  def setStage(stage: Stage)
  def updateImage()
}

@sfxml
class MainController(selectBtn: Button, cropBtn: Button, shownImage: ImageView)
  extends MainInterface {
  var stage: Option[Stage] = None
  val engine = new Engine()

  override def setStage(stage: Stage): Unit = this.stage = Some(stage)

  override def open(): Unit = FileBrowser.importFile(stage) match {
    case f: File => engine.setImage(FileImport.loadPicture(f)); updateImage()
    case _ => println("Canceled")
  }

  override def save(): Unit = engine.getImage match {
    case Some(bi) => FileExport.saveFile(bi, "jpg")
    case _ => println("Nothing to save")
  }

  override def saveAs(): Unit = ???

  // todo - check if there is unsaved work somehow
  override def close(): Unit = ExitController.fireEvent(stage)

  override def testMe(): Unit = {
    engine.pictureTest()
    updateImage()
  }

  override def updateImage(): Unit = engine.convertImageToFx(shownImage)
}

object MainControllerApp extends JFXApp3 {
  override def start(): Unit = {
    val url: URL = getClass.getResource("resources/main.fxml")
    val loader = new FXMLLoader(url, new DependenciesByType(Map()))

    loader.load()
    val root: Parent = loader.getRoot[jfxs.Parent]
    val mainController: MainInterface = loader.getController[MainInterface]

    stage = new JFXApp3.PrimaryStage() {
      title = "FPhotoshop"
      scene = new Scene(root)
      filterEvent(WindowEvent.WindowCloseRequest) {
        event: WindowEvent => ExitController.handleExitEvent(stage, event)
      }
    }
    mainController.setStage(stage)
  }
}

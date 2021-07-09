package frontend.main

import backend.engine.Engine
import backend.io.{FileExport, FileImport}
import frontend.exit.ExitController
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Slider}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
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
  def rotateRight(): Unit
  def rotateLeft(): Unit
  def testMe(): Unit
  def setStage(stage: Stage)
  def getStage: Stage
  def updateImage()
}

@sfxml
class MainController(shownImage: ImageView, centerPane: StackPane, zoomSlider: Slider, openOnStack: Button)
  extends MainInterface {
  var stage: Option[Stage] = None

  override def setStage(stage: Stage): Unit = this.stage = Some(stage)

  override def getStage: Stage = stage.getOrElse(throw new IllegalStateException())

  def showImageHideButton(): Unit = {
    def hideButtonOnStack(): Unit = {
      openOnStack.setDisable(true)
      openOnStack.setVisible(false)
    }
    def showImage(): Unit = {
      shownImage.setDisable(false)
      shownImage.setVisible(true)
    }
    hideButtonOnStack()
    showImage()
  }

  override def open(): Unit = FileImport.importFile(getStage) match {
    case f: File =>
      Engine.setImageFile(f)
      Engine.setImage(FileImport.loadImage(f))
      showImageHideButton()
      updateImage()
    case _ => println("Canceled")
  }

  override def save(): Unit = FileExport.tryToSave(None)

  override def saveAs(): Unit = FileExport.tryToSave(stage)

  // todo - check if there is unsaved work somehow
  override def close(): Unit = getStage.fireEvent(new WindowEvent(getStage, WindowEvent.WindowCloseRequest))

  override def testMe(): Unit = Engine.getImageOption match {
    case Some(_) =>
      Engine.pictureTest()
      updateImage()
    case None => println("Nothing to test")
  }

  override def updateImage(): Unit = Engine.updateImage(shownImage, centerPane)
  override def rotateRight(): Unit = rotate(true)
  override def rotateLeft(): Unit = rotate(false)
  def rotate(isRight: Boolean): Unit = {
    if (!shownImage.isDisabled) {
      Engine.rotateImage(shownImage, isRight)
      updateImage()
    }
    else println("Nothing to rotate")
  }
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

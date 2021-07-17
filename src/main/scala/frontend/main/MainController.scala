package frontend.main

import backend.engine.Engine
import backend.io.{FileExport, SaveAs}
import backend.layers.{Image2, ImageManager, LayerManager}
import frontend.exit.ExitController
import frontend.layers.LayerCardView
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView, Slider}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.stage.{Stage, WindowEvent}
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL

trait MainInterface {
  def open(): Unit
  def save(): Unit
  def saveAs(): Unit
  def close(): Unit
  def rotateRight(): Unit
  def rotateLeft(): Unit
  def testMe(): Unit
  def layerTest(): Unit
  def updateImage()
}

@sfxml
class MainController(borderPane: BorderPane, centerPane: StackPane, zoomSlider: Slider, openOnStack: Button, layers: ListView[LayerCardView])
  extends MainInterface {
  // todo learn how to avoid vars
  val stage: Stage = MainControllerApp.stage
  val layerManager: LayerManager = new LayerManager(layers)

  override def open(): Unit = {
    def hideButtonOnStack(): Unit = {
      openOnStack.setDisable(true)
      openOnStack.setVisible(false)
    }
    hideButtonOnStack()
    ImageManager.add(new Image2())
    ImageManager.update(centerPane)
  }

  // FileImport.importFile(getStage) match {
  //    case f: File =>
  //      val layerCard = new LayerCard("layer", f)
  //      layerManager.addCard(layerCard)
  //      showImageHideButton()
  //      updateImage()
  //    case _ => println("Canceled")
  //  }

  override def save(): Unit = FileExport.tryToSave1(None)

  override def saveAs(): Unit = FileExport.tryToSave(SaveAs())(ImageManager.imageBuffer.head) // FileExport.tryToSave(stage)

  // todo - check if there is unsaved work somehow
  override def close(): Unit = stage.fireEvent(new WindowEvent(stage, WindowEvent.WindowCloseRequest))

  override def testMe(): Unit = Engine.getImageOption match {
    case Some(_) =>
      Engine.pictureTest()
      updateImage()
    case None => println("Nothing to test")
  }

  override def updateImage(): Unit = ??? // Engine.updateImage(shownImage, centerPane)

  override def rotateRight(): Unit = rotate(true)
  override def rotateLeft(): Unit = rotate(false)

  def rotate(isRight: Boolean): Unit = {
//    if (!shownImage.isDisabled) {
//      Engine.rotateImage(shownImage, isRight)
//      updateImage()
//    }
//    else println("Nothing to rotate")
  }

  override def layerTest(): Unit = {
//    layerManager.addCard("Layer_", Engine.getImage)
//    LayerCardView.update(layers)
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
        event: WindowEvent => ExitController.handleExitEvent(event)
      }
    }
  }
}

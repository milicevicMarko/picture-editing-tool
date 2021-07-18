package frontend.main

import backend.engine.Engine
import backend.io.{FileExport, SaveAs}
import backend.layers.ImageManager.imageBuffer
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.utils.UIUtils
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView}
import scalafx.scene.layout.StackPane
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
class MainController(centerPane: StackPane,openOnStack: Button, layers: ListView[Image])
  extends MainInterface {
  val stage: Stage = MainControllerApp.stage


  def updateImages(): Unit = {
    centerPane.children.clear()
    for (img <- imageBuffer) yield {
      // todo no need to clear and readd everything, move this logic to the image creation!
      UIUtils.bindImageViewToPane(img.imageView)(centerPane)
      centerPane.children.add(img.imageView)
    }
  }

  override def open(): Unit = {
    def hideButtonOnStack(): Unit = {
      openOnStack.setDisable(true)
      openOnStack.setVisible(false)
    }

    hideButtonOnStack()
    ImageManager.addNewImage()
    updateImages()
  }

  // todo
  override def save(): Unit = FileExport.tryToSave1(None)

  // todo
  override def saveAs(): Unit = FileExport.tryToSave(SaveAs())(ImageManager.imageBuffer.head) // FileExport.tryToSave(stage)

  // todo - check if there is unsaved work somehow
  override def close(): Unit = stage.fireEvent(new WindowEvent(stage, WindowEvent.WindowCloseRequest))

  // todo or not
  override def testMe(): Unit = Engine.getImageOption match {
    case Some(_) =>
      Engine.pictureTest()
      updateImage()
    case None => println("Nothing to test")
  }

  override def updateImage(): Unit = updateImages

  override def rotateRight(): Unit = rotate(true)
  override def rotateLeft(): Unit = rotate(false)

  def rotate(isRight: Boolean): Unit = {
    if (openOnStack.isDisabled) {
      Image.rotateImage(ImageManager.getSelectedImage, isRight)
      updateImage()
    }
    else println("Nothing to rotate")
  }

  override def layerTest(): Unit = {
//    layerManager.addCard("Layer_", Engine.getImage)
//    LayerCardView.update(layers)
    // todo - error handling
    ImageManager.swap(ImageManager.imageAt(0), ImageManager.imageAt(1))
    updateImage()
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

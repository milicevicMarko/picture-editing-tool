package frontend.main

import backend.engine.Engine
import backend.io.{FileExport, SaveAs}
import backend.layers.ImageManager.imageBuffer
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.layers.{CardListView, CardView}
import frontend.utils.UIUtils
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView}
import scalafx.scene.layout.{StackPane, VBox}
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
}

@sfxml
class MainController(centerPane: StackPane, openOnStack: Button, layers: ListView[CardView], testVBox: VBox)
  extends MainInterface {
  val stage: Stage = MainControllerApp.stage
  val cardListView: CardListView = new CardListView(layers)

  def showOpenButton(show: Boolean): Unit = {
    openOnStack.setVisible(show)
    openOnStack.setDisable(!show)
    println(s"show $show")
  }

  override def open(): Unit = {
    ImageManager.addNewImage() match {
      case Some(image) =>
        UIUtils.bindImageViewToPane(image.imageView)(centerPane)
        centerPane.children.addOne(image.imageView)
        showOpenButton(false)
      case None => println("Canceled")
    }
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
    case None => println("Nothing to test")
  }

  override def rotateRight(): Unit = rotate(true)
  override def rotateLeft(): Unit = rotate(false)

  def rotate(isRight: Boolean): Unit = {
    if (openOnStack.isDisabled) {
      ImageManager.rotate(isRight)
    }
    else println("Nothing to rotate")
  }

  def updateLayers(): Unit = {
    centerPane.children.clear()
    for (img <- imageBuffer) yield centerPane.children.add(img.imageView)
  }

  override def layerTest(): Unit = {
    // todo - error handling
    ImageManager.swap(ImageManager.imageAt(0), ImageManager.imageAt(1))
    updateLayers()
  }
}

object MainControllerApp extends JFXApp3 {
  override def start(): Unit = {
    val url: URL = getClass.getResource("resources/main.fxml")
    val loader = new FXMLLoader(url, new DependenciesByType(Map()))

    loader.load()
    val root: Parent = loader.getRoot[jfxs.Parent]
    loader.getController[MainInterface]

    stage = new JFXApp3.PrimaryStage() {
      title = "FPhotoshop"
      scene = new Scene(root)
      filterEvent(WindowEvent.WindowCloseRequest) {
        event: WindowEvent => ExitController.handleExitEvent(event)
      }
    }
  }
}

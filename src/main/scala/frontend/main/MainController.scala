package frontend.main

import backend.engine.Engine
import backend.io.FileBrowser
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.layers.CardListView
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView}
import scalafx.scene.layout.{Pane, StackPane}
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
  def swap(): Unit
  def layerTest(): Unit
}

@sfxml
class MainController(centerPane: StackPane, openOnStack: Button, layers: ListView[Image], upperSplit: Pane)
  extends MainInterface {
  val stage: Stage = MainControllerApp.stage
  val cardListView: CardListView = new CardListView(layers)

  def showOpenButton(show: Boolean): Unit = {
    openOnStack.setVisible(show)
    openOnStack.setDisable(!show)
  }

  override def open(): Unit = {
    // todo check if import can return multiple paths, go trough path and add files
    FileBrowser.chooseImportMultiplePath() match {
      case paths: List[String] => ImageManager add paths map(image => image addToPane centerPane)
      case Nil => println("Canceled")
    }
  }

  // todo
  override def save(): Unit = ???

  // todo
  override def saveAs(): Unit = ???

  // todo - check if there is unsaved work somehow
  override def close(): Unit = stage.fireEvent(new WindowEvent(stage, WindowEvent.WindowCloseRequest))

  // todo or not
  override def testMe(): Unit = Engine.getImageOption match {
    case Some(_) =>
      Engine.pictureTest()
    case None => println("Nothing to test")
  }

  override def rotateRight(): Unit = ImageManager.rotate(true)

  override def rotateLeft(): Unit = ImageManager.rotate(false)

  def updateLayers(): Unit = centerPane.children = ImageManager.allImageViews

  override def swap(): Unit = {
    ImageManager.swap()
    updateLayers()
  }

  override def layerTest(): Unit = {
    println("Testing")
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

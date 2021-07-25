package frontend.main

import backend.engine.Engine
import backend.io.FileBrowser
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.layers.CardListView
import javafx.collections.ListChangeListener
import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView}
import javafx.scene.input.MouseEvent
import scalafx.event.EventType
import scalafx.scene.layout.{AnchorPane, Pane, StackPane}
import scalafx.scene.paint.Paint
import scalafx.scene.shape.Rectangle
import scalafx.stage.{Stage, WindowEvent}
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL
import scala.collection.mutable.ListBuffer

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
class MainController(selectPane: AnchorPane, centerPane: StackPane, openOnStack: Button, layers: ListView[Image], upperSplit: Pane)
  extends MainInterface {
  val stage: Stage = MainControllerApp.stage
  val cardListView: CardListView = new CardListView(layers)

  val selectRectangles = new ListBuffer[Rectangle]
  def selectRectangle: Rectangle = selectRectangles.last

  def newRectangle(xx: Double, yy: Double): Unit = selectRectangles.addOne(new Rectangle() {
    opacity = 0.5
    visible = true
    fill = javafx.scene.paint.Color.DODGERBLUE
    x = xx
    y = yy
  })

  // todo whole pane moves for some reason
  selectPane.addEventFilter[MouseEvent](MouseEvent.ANY, e => e.getEventType match {
    case MouseEvent.MOUSE_PRESSED =>
      newRectangle(e.getX, e.getY)
      selectPane.children.addOne(selectRectangle)
    case MouseEvent.MOUSE_DRAGGED =>
      val dx = e.getX - selectRectangle.getX
      selectRectangle.translateX = if (dx < 0) dx else 0
      selectRectangle.width = dx.abs

      val dy = e.getY - selectRectangle.getY
      selectRectangle.translateY = if (dy < 0) dy else 0
      selectRectangle.height = dy.abs

    case MouseEvent.MOUSE_RELEASED =>
      // todo select pixels beneath
      println(selectRectangle)
      println("done")
    case _ =>
  })

  ImageManager.imageBuffer.addListener(new ListChangeListener[Image] {
    override def onChanged(change: ListChangeListener.Change[_ <: Image]): Unit = {
      while (change.next) {
        if (change.wasAdded() || change.wasRemoved())
          centerPane.children = ImageManager.imageBuffer.toList.distinct.map(img => (img bind centerPane).imageView)
        if (centerPane.children.isEmpty)
          selectPane.children = openOnStack
        else
          selectPane.children.remove(openOnStack)
      }
    }
  })

  override def open(): Unit = {
    FileBrowser.chooseImportMultiplePath() match {
      case paths: List[String] => ImageManager add paths
      case Nil => println("Canceled")
    }
  }

  override def save(): Unit = ???

  override def saveAs(): Unit = ???

  // todo - check if there is unsaved work somehow
  override def close(): Unit = stage.fireEvent(new WindowEvent(stage, WindowEvent.WindowCloseRequest))

  override def testMe(): Unit = Engine.getImageOption match {
    case Some(_) =>
      Engine.pictureTest()
    case None => println("Nothing to test")
  }

  override def rotateRight(): Unit = ImageManager.rotate(true)

  override def rotateLeft(): Unit = ImageManager.rotate(false)

  def updateLayers(): Unit = {
    centerPane.children.clear()
    centerPane.children = ImageManager.allImageViews
  }

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

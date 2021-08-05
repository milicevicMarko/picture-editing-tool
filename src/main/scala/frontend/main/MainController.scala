package frontend.main

import backend.engine.Operations
import backend.io.FileBrowser
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.layers.CardListView
import javafx.collections.ListChangeListener
import javafx.scene.{Parent, input}
import javafx.{scene => jfxs}
import javafx.scene.paint.Color
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ColorPicker, ListView, ToggleButton}
import javafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, StackPane}
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
  def setFillColor(): Unit
  def toggleFillColor(): Unit

  def addOp(): Unit
  def subOp(): Unit
  def invSubOp(): Unit
  def mulOp(): Unit
  def divOp(): Unit
  def invDivOp(): Unit

  def invertOp(): Unit
  def greyscaleOp(): Unit
}

@sfxml
class MainController(selectPane: AnchorPane, centerPane: StackPane, openOnStack: Button, layers: ListView[Image],
                     selectToggleButton: ToggleButton, fillToggleButton: ToggleButton, colorBox: ColorPicker)
  extends MainInterface {
  val stage: Stage = MainControllerApp.stage
  val cardListView: CardListView = new CardListView(layers)

  val selectRectangles = new ListBuffer[Rectangle]
  def selectRectangle: Rectangle = selectRectangles.last

  def newRectangle(xx: Double, yy: Double): Unit = {
    val r = new Rectangle() {
      visible = true
      stroke = Color.BLACK
      strokeDashArray = Seq(2d)
      fill = Color.TRANSPARENT
      strokeWidth = 0.5
      x = xx
      y = yy
    }

    r.setOnMouseClicked(e => {
      if (e.getButton == input.MouseButton.SECONDARY) deleteSelectRectangle(r)
      else if (e.getButton == input.MouseButton.PRIMARY && fillColor.isDefined) {
        r.setFill(fillColor.get)
        fillColor = None
        r.setOpacity(1)
        r.strokeWidth = 0
      }
    })
    selectRectangles.addOne(r)
  }

  def deleteSelectRectangle(rect: Rectangle): Unit = {
    selectPane.children.remove(rect)
    selectRectangles.remove(selectRectangles.indexOf(rect))
  }

  var fillColor: Option[Color] = None
  val devHack = true
  def centerPaneNonEmpty: Boolean = centerPane.children.nonEmpty || devHack

  selectPane.addEventFilter[MouseEvent](MouseEvent.ANY, e => if (selectToggleButton.isSelected && centerPaneNonEmpty) e.getEventType match {
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
      if (selectRectangle.getWidth <= 5 && selectRectangle.getHeight <= 5)
        selectRectangles.remove(selectRectangles.size - 1)
      else
        println(selectRectangles.size)
      println("done")
    case _ =>
  })

  def toggleSelect(): Unit = {
    if (selectToggleButton.isSelected)
      if (fillToggleButton.isSelected) {
        fillToggleButton.selected = false
        toggleFillColor()
      }
  }

  def toggleFillColor(): Unit = {
    colorBox.setVisible(fillToggleButton.isSelected)
    colorBox.setDisable(!fillToggleButton.isSelected)
    if (!fillToggleButton.isSelected)
      fillColor = None
    else if (selectToggleButton.isSelected)
        selectToggleButton.selected = false
  }

  def setFillColor(): Unit = fillColor = Some(colorBox.getValue)

  ImageManager.imageBuffer.addListener(new ListChangeListener[Image] {
    override def onChanged(change: ListChangeListener.Change[_ <: Image]): Unit = {
      while (change.next) {
        if (change.wasAdded() || change.wasRemoved())
          centerPane.children = ImageManager.imageBuffer.toList.distinct.map(img => (img bindTo centerPane).imageView)
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

//    Engine.getImageOption match {
//    case Some(_) =>
//      Engine.pictureTest()
//    case None => println("Nothing to test")
//  }

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


  override def testMe(): Unit = ImageManager.operate(Operations.div(5))

  // refresh
  override def layerTest(): Unit = centerPane.children = ImageManager.imageBuffer.toList.distinct.map(img => (img bindTo centerPane).imageView)

  // todo do this with select and with value from text field
  // todo art
  override def addOp(): Unit = ImageManager.operate(Operations.add(0.005))

  override def subOp(): Unit = ImageManager.operate(Operations.sub(0.005))

  override def invSubOp(): Unit = ImageManager.operate(Operations.invSub(1))

  override def mulOp(): Unit = ImageManager.operate(Operations.mul(0.005))

  override def divOp(): Unit = ImageManager.operate(Operations.div(5))

  override def invDivOp(): Unit = ImageManager.operate(Operations.invDiv(1))

  override def invertOp(): Unit = ImageManager.operate(Operations.invSub(1))

  override def greyscaleOp(): Unit = ImageManager.operate(Operations.greyscale())
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

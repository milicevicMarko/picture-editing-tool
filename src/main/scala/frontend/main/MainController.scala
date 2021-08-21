package frontend.main

import backend.engine.{BaseOperation, CompositeDB, Operations}
import backend.io.{FileBrowser, ResourceManager}
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.layers.CardListView
import frontend.operations.OperationsController
import javafx.collections.ListChangeListener
import javafx.scene.{Parent, input}
import javafx.{scene => jfxs}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ColorPicker, ListView, TextField, ToggleButton}
import scalafx.scene.layout.{AnchorPane, StackPane}
import scalafx.scene.shape.Rectangle
import scalafx.stage.{Modality, Stage, WindowEvent}
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
  def debugCursor(): Unit
  def swap(): Unit
  def refresh(): Unit
  def print(): Unit
  def flatten(): Unit

  def setFillColor(): Unit
  def toggleFillColor(): Unit

  def addOp(): Unit
  def subOp(): Unit
  def invSubOp(): Unit
  def mulOp(): Unit
  def divOp(): Unit
  def invDivOp(): Unit

  def powOp(): Unit
  def logOp(): Unit
  def maxOp(): Unit
  def minOp(): Unit
  def absOp(): Unit

  def invertOp(): Unit
  def greyscaleOp(): Unit
  def openComposer(): Unit
  def useComposite(): Unit
  def removeComposite(): Unit
}

@sfxml
class MainController(selectPane: AnchorPane, centerPane: StackPane, mainPane: StackPane, openOnStack: Button, layers: ListView[Image],
                     selectToggleButton: ToggleButton, fillToggleButton: ToggleButton, colorBox: ColorPicker,
                     textField: TextField, compositeList: ListView[String])
  extends MainInterface {
  val stage: Stage = MainControllerApp.stage
  val cardListView: CardListView = new CardListView(layers)
  updateComposites()


  // ----------------------------------------
  // central image listeners
  // ----------------------------------------
  ImageManager.imageBuffer.addListener(new ListChangeListener[Image] {
    override def onChanged(change: ListChangeListener.Change[_ <: Image]): Unit = {
      while (change.next) {
        if (change.wasAdded() || change.wasRemoved())
          centerPane.children = ImageManager.imageBuffer.toList.distinct.map(img => (img bindTo centerPane).imageView)
        if (centerPane.children.isEmpty) {
          selectPane.children.clear()
          selectPane.children.addAll(openOnStack, colorBox)
          selectRectangles.clear()
        } else
          selectPane.children.remove(openOnStack)
      }
    }
  })
  // ----------------------------------------
  // selection
  // ----------------------------------------
  val selectRectangles = new ListBuffer[Rectangle]
  def currentSelection: Rectangle = selectRectangles.last

  def newRectangle(xx: Double, yy: Double, ww: Double = 0, hh: Double = 0): Unit = {
    val r = new Rectangle() {
      visible = true
      stroke = Color.BLACK
      strokeDashArray = Seq(2d)
      fill = Color.TRANSPARENT
      strokeWidth = 0.5
      x = xx
      y = yy
      width = ww
      height = hh
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

    addSelectRectangle(r)
  }

  def addSelectRectangle(rectangle: Rectangle): Unit = {
    selectPane.children.addOne(rectangle)
    selectRectangles.addOne(rectangle)
  }

  def deleteSelectRectangle(rect: Rectangle): Unit = {
    selectPane.children.remove(rect)
    selectRectangles.remove(selectRectangles.indexOf(rect))
  }

  var fillColor: Option[Color] = None
  val devHack = true
  def centerPaneNonEmpty: Boolean = centerPane.children.nonEmpty || devHack

  selectPane.addEventFilter[MouseEvent](MouseEvent.ANY, e => if (selectToggleButton.isSelected && centerPaneNonEmpty) e.getEventType match {
    case MouseEvent.MOUSE_PRESSED => newRectangle(e.getX, e.getY)
    case MouseEvent.MOUSE_DRAGGED =>
      val dx = e.getX - currentSelection.getX
      currentSelection.translateX = if (dx < 0) dx else 0
      currentSelection.width = dx.abs

      val dy = e.getY - currentSelection.getY
      currentSelection.translateY = if (dy < 0) dy else 0
      currentSelection.height = dy.abs

    case MouseEvent.MOUSE_RELEASED =>
//      val r = currentSelection
//      println(s"rect: [${r.getX}, ${r.getY}], [${r.getX + r.getWidth}, ${r.getY + r.getHeight}]")

      // ignore clicks
      if (currentSelection.getWidth <= 5 && currentSelection.getHeight <= 5)
        selectRectangles.remove(selectRectangles.size - 1)
      // switch x,y of rectangle
      else if (e.getX < currentSelection.getX || e.getY < currentSelection.getY) {
          val old = currentSelection
          newRectangle(e.getX, e.getY, currentSelection.getWidth, currentSelection.getHeight)
          deleteSelectRectangle(old)
        }
    case _ =>
  })

  def toggleSelect(): Unit = {
    if (selectToggleButton.isSelected)
      if (fillToggleButton.isSelected) {
        fillToggleButton.selected = false
        toggleFillColor()
      }
  }

  // ----------------------------------------
  // open, save, save as
  // ----------------------------------------
  override def open(): Unit = {
    FileBrowser.chooseImportMultiplePath() match {
      case paths: List[String] => ImageManager add paths
      case _ => println("Canceled")
    }
  }

  override def save(): Unit = ???

  override def saveAs(): Unit = ???

  // todo - check if there is unsaved work somehow
  override def close(): Unit = stage.fireEvent(new WindowEvent(stage, WindowEvent.WindowCloseRequest))

  // ----------------------------------------
  // operations
  // ----------------------------------------
  override def rotateRight(): Unit = ImageManager.rotate(true)

  override def rotateLeft(): Unit = ImageManager.rotate(false)

  def updateLayers(): Unit = {
    centerPane.children.clear()
    centerPane.children = ImageManager.allImageViews
  }

  override def swap(): Unit = ImageManager.swap()

  override def testMe(): Unit = ResourceManager.test() //println("Nothing is set for debugging")

  var debugMouse: Boolean = false
  override def debugCursor(): Unit = {
    if (debugMouse) {
      selectPane.removeEventFilter[MouseEvent](MouseEvent.MOUSE_PRESSED, _)
      debugMouse = false
    } else {
      debugMouse = true
      selectPane.addEventFilter[MouseEvent](MouseEvent.MOUSE_PRESSED, e => {
        val a: (Double, Double) = (e.getX, e.getY)
        val b: (Double, Double) = (e.getSceneX, e.getSceneY)
        val c: (Double, Double) = (e.getScreenX, e.getScreenY)
        println(s"[click]\tregular: $a\tscene: $b\tscreen: $c")
      })
    }
  }

  override def refresh(): Unit = {
    ImageManager.activated.foreach(i => i.refresh())
    centerPane.children = ImageManager.imageBuffer.toList.distinct.map(img => (img bindTo centerPane).imageView)
  }

  override def print(): Unit = ImageManager.selected.foreach(i => println(s"${i.getPixel(20, 20)}\t${i.getPixel(200, 200)}"))

  // ----------------------------------------
  // right operations
  // ----------------------------------------
  override def addOp(): Unit = operate(Operations.add)

  override def subOp(): Unit = operate(Operations.sub)

  override def invSubOp(): Unit = operate(Operations.invSub)

  override def mulOp(): Unit = operate(Operations.mul)

  override def divOp(): Unit = operate(Operations.div)

  override def invDivOp(): Unit = operate(Operations.invDiv)

  override def powOp(): Unit = operate(Operations.pow)

  override def logOp(): Unit = operate(Operations.log)

  override def maxOp(): Unit = operate(Operations.max)

  override def minOp(): Unit = operate(Operations.min)

  // todo check this
  override def absOp(): Unit = ImageManager.operate(Operations.abs(), selectRectangles.toList)

  override def greyscaleOp(): Unit = ImageManager.operate(Operations.greyscale(), selectRectangles.toList)

  override def invertOp(): Unit = ImageManager.operate(Operations.invert(), selectRectangles.toList)

  override def flatten(): Unit = {
    tryToFill()
    ImageManager.flatten()
  }

  def tryToFill(): Unit = if (selectRectangles.count(r => r.getFill != null) > 0) fillSelection()

  def fillSelection(): Unit = ImageManager.operate(Operations.fill(), selectRectangles.filter(r => r.getFill != null).toList)

  def toggleFillColor(): Unit = {
    colorBox.setVisible(fillToggleButton.isSelected)
    colorBox.setDisable(!fillToggleButton.isSelected)
    if (!fillToggleButton.isSelected)
      fillColor = None
    else if (selectToggleButton.isSelected)
      selectToggleButton.selected = false
  }

  def setFillColor(): Unit = fillColor = Some(colorBox.getValue)

  def operate(op: Double => BaseOperation): Unit = readTextField() match {
    case Some(value) => ImageManager.operate(op(value), selectRectangles.toList)
    case None if !Operations.needsArgument(op) => ImageManager.operate(op(0), selectRectangles.toList)
    case None => println("Please enter text field value")
  }

  def readTextField(): Option[Double] = {
    if (textField.getText.matches("\\d*(\\.\\d+|)")) Some(textField.getText.toDouble)
    else None
  }

  // ----------------------------------------
  // op composer
  // ----------------------------------------
  override def openComposer(): Unit = {
    val loader = new FXMLLoader(getClass.getResource("../operations/resources/operations.fxml"), new DependenciesByType(Map()))
    loader.load()
    val root: Parent = loader.getRoot[jfxs.Parent]
    loader.getController[OperationsController]

    val dialogStage = new Stage()
    dialogStage.scene = new Scene(root)
    dialogStage.initOwner(stage)
    dialogStage.initModality(Modality.ApplicationModal)
    dialogStage.title = "Operation Composer"
    dialogStage.showAndWait()
    updateComposites()
  }

  def updateComposites(): Unit = {
    compositeList.items = CompositeDB.getObservables
  }

  override def useComposite(): Unit = {
    if (compositeList.getSelectionModel.getSelectedItems.size() > 0) {
      val name = compositeList.getSelectionModel.getSelectedItem
      compositeList.getSelectionModel.clearSelection()
      val composite = CompositeDB.findComposite(name)
      ImageManager.operate(composite, selectRectangles.toList)
    }
  }

  override def removeComposite(): Unit = {
    if (compositeList.getSelectionModel.getSelectedItems.size() > 0) {
      val name = compositeList.getSelectionModel.getSelectedItem
      compositeList.getSelectionModel.clearSelection()
      CompositeDB.removeComposite(name)
      updateComposites()
    }
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
//      maximized = true
      filterEvent(WindowEvent.WindowCloseRequest) {
        event: WindowEvent => ExitController.handleExitEvent(event)
      }
    }
  }
}

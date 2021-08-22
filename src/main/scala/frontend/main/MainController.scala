package frontend.main

import backend.engine.{BaseOperation, CompositeDB, Operations, Selection, SelectionManager}
import backend.io.{FileBrowser, ResourceManager}
import backend.layers.{Image, ImageManager}
import frontend.exit.ExitController
import frontend.layers.CardListView
import frontend.operations.OperationsController
import javafx.collections.ListChangeListener
import javafx.scene.Parent
import javafx.{scene => jfxs}
import javafx.scene.input.MouseEvent
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
          SelectionManager.clear()
        } else {
          selectPane.children.remove(openOnStack)
        }
      }
    }
  })
  // ----------------------------------------
  // selection
  // ----------------------------------------

  SelectionManager.buffer.addListener(new ListChangeListener[Selection] {
    override def onChanged(change: ListChangeListener.Change[_ <: Selection]): Unit = {
      while (change.next) {
        if (change.wasAdded() || change.wasRemoved()) {
          selectPane.children.clear()
          selectPane.children = SelectionManager.buffer.map(selection => selection.rectangle).toList
          selectPane.children.addOne(colorBox)
        }
      }
      if (centerPane.children.isEmpty)
        selectPane.children.add(openOnStack)
    }
  })

  def newRectangle(xx: Double, yy: Double, ww: Double = 0, hh: Double = 0): Rectangle = {
    val r = SelectionManager.newRectangle(xx, yy, ww, hh)
    SelectionManager.addActionListeners(r)
    selectPane.children.add(r)
    r
  }

  var currentSelect: Option[Rectangle] = None

  selectPane.addEventFilter[MouseEvent](MouseEvent.ANY, e => if (selectToggleButton.isSelected && centerPane.children.nonEmpty) e.getEventType match {
    case MouseEvent.MOUSE_PRESSED =>
      currentSelect = Some(newRectangle(e.getX, e.getY))

    case MouseEvent.MOUSE_DRAGGED =>
      val selected = currentSelect.get
      val dx = e.getX - selected.getX
      selected.translateX = if (dx < 0) dx else 0
      selected.width = dx.abs

      val dy = e.getY - selected.getY
      selected.translateY = if (dy < 0) dy else 0
      selected.height = dy.abs

    case MouseEvent.MOUSE_RELEASED =>
      if (rectangleIsACLick(currentSelect.get)) {
        // ignore clicks
        selectPane.children.remove(currentSelect.get)
        currentSelect = None
      } else if (e.getX < currentSelect.get.getX || e.getY < currentSelect.get.getY) {
        // switch x,y of rectangle
        val old = currentSelect
        val newRect = newRectangle(e.getX, e.getY, currentSelect.get.getWidth, currentSelect.get.getHeight)
        selectPane.children.remove(old)
        currentSelect = Some(newRect)
      }
      currentSelect match {
        case Some(_) => SelectionManager.add(currentSelect.get)
        case None =>
      }

    case _ =>
  })

  def rectangleIsACLick(rectangle: Rectangle): Boolean = rectangle.getWidth <= 5 && rectangle.getHeight <= 5

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
  override def absOp(): Unit = ImageManager.operate(Operations.abs(), SelectionManager.buffer.toList)

  override def greyscaleOp(): Unit = ImageManager.operate(Operations.greyscale(), SelectionManager.buffer.toList)

  override def invertOp(): Unit = ImageManager.operate(Operations.invert(), SelectionManager.buffer.toList)

  override def flatten(): Unit = {
    tryToFill()
    ImageManager.flatten()
  }

  def tryToFill(): Unit = SelectionManager.tryToFill()

  def toggleFillColor(): Unit = {
    colorBox.setVisible(fillToggleButton.isSelected)
    colorBox.setDisable(!fillToggleButton.isSelected)
    if (!fillToggleButton.isSelected)
      SelectionManager.fillColor = None
    else if (selectToggleButton.isSelected)
      selectToggleButton.selected = false
  }

  def setFillColor(): Unit = SelectionManager.fillColor = Some(colorBox.getValue)

  def operate(op: Double => BaseOperation): Unit = readTextField() match {
    case Some(value) => ImageManager.operate(op(value), SelectionManager.buffer.toList)
    case None if !Operations.needsArgument(op) => ImageManager.operate(op(0), SelectionManager.buffer.toList)
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
      ImageManager.operate(composite, SelectionManager.buffer.toList)
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

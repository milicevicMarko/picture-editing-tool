package backend.engine

import backend.layers.{Image, RGB}
import javafx.collections.ObservableList
import scalafx.collections.ObservableBuffer
import javafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

abstract case class BaseOperation(name: String) {
  def operate(rgb: RGB): RGB

  def inSelection(x: Double, y: Double, layoutX: Double, layoutY: Double, r: Rectangle): Boolean =
    x >= r.getX - layoutX && x <= r.getX + r.getWidth - layoutX && y>= r.getY - layoutY && y <= r.getY + r.getHeight - layoutY

  def isPixelInSelection(x: Double, y: Double, layoutX: Double, layoutY: Double, selection: List[Rectangle]): Boolean = {
    @tailrec
    def iterSelections(x: Double, y: Double, selections: List[Rectangle]): Boolean = selections match {
      case s::ss => if (!inSelection(x, y, layoutX, layoutY, s)) iterSelections(x, y, ss) else true
//      case List(s) => inSelection(x, y, layoutX s) // todo
      case Nil => false
    }
    iterSelections(x, y, selection)
  }

  def inSelection2(pixelCoordinates: (Double, Double), layoutOffsets: (Double, Double), r: Rectangle): Boolean = {
    pixelCoordinates._1 + layoutOffsets._1 >= r.getX && pixelCoordinates._1 + layoutOffsets._1  <= r.getX + r.getWidth && pixelCoordinates._2 + layoutOffsets._2 >= r.getY && pixelCoordinates._2 + layoutOffsets._2 <= r.getY + r.getHeight
  }
  def isPixelInSelection2(pixelCoordinates: (Double, Double), layoutOffsets: (Double, Double), selection: List[Rectangle]): Boolean = {
    @tailrec
    def iterSelections(pixelCoordinates: (Double, Double), selections: List[Rectangle]): Boolean = selections match {
      case s::ss => if (!inSelection2(pixelCoordinates, layoutOffsets, s)) iterSelections(pixelCoordinates, ss) else true
      //      case List(s) => inSelection(x, y, layoutX s) // todo
      case Nil => false
    }
    iterSelections(pixelCoordinates, selection)
  }

  def apply(image: Image, selection: List[Rectangle]): Image = {
    val (positionX, positionY) = image.getLayoutPosition
    val layoutOffset = image.getLayoutPosition
    val img = image.getBufferedImage
    var pxs = 0
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
          // replace posX, posY with rects
         if selection.isEmpty || isPixelInSelection2(image.relativePosition(x, y), layoutOffset, selection)) {
      img.setRGB(x, y, operate(img.getRGB(x, y)).limit())
      pxs = pxs + 1
    }
    println(pxs)
    image.deepCopy()
  }
  override def toString: String = name
}

class SimpleOperation(name: String, operation: RGB => RGB) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = operation(rgb)
}

class FillOperation() extends BaseOperation("fill") {
  implicit def intRGBFromSelectionFill(rectangle: Rectangle): Int = {
    val c: Color = rectangle.getFill.asInstanceOf[Color]
    new RGB(c.getRed, c.getGreen, c.getBlue)
  }
  override def apply(image: Image, selection: List[Rectangle]): Image = {
    val (positionX, positionY) = image.getLayoutPosition
    val img = image.getBufferedImage
    for (rect <- selection;
         x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if inSelection(x, y, positionX, positionY, rect)) {
      img.setRGB(x, y, rect)
    }
    image.deepCopy()
  }

  override def operate(rgb: RGB): RGB = rgb
}

class CompositeOperation(name: String, operations: List[BaseOperation]) extends BaseOperation(name) {
  override def operate(acc: RGB): RGB = operations.foldLeft(acc)((rgb, bo) => bo.operate(rgb))

  override def toString: String = {
    super.toString
    operations.foldLeft("")((_, op) => " " + op.toString)
  }
}

class FilterOperation(name: String) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = rgb
  // todo wtf should i do ahhaha
}

object CompositeDB {
  val composites: ListBuffer[CompositeOperation] = new ListBuffer[CompositeOperation]

  def getObservables: ObservableList[String] = {
    val buffer: ObservableList[String] = new ObservableBuffer[String]()
    composites.foreach(comp => buffer.add(comp.name))
    buffer
  }

  def findComposite(name: String): BaseOperation = composites.find(c => c.name == name).get
  def removeComposite(name: String): Unit = composites.remove(composites.indexOf(findComposite(name)))
}

object Operations {
  implicit def simple(f: RGB=>RGB)(name: String): BaseOperation = new SimpleOperation(name, f)

  def add(value: Double): BaseOperation = new SimpleOperation("add", (i: RGB) => i + value)
  def sub(value: Double): BaseOperation = new SimpleOperation("sub", (i: RGB) => i - value)
  def invSub(value: Double): BaseOperation = new SimpleOperation("inv sub", (i: RGB) => i -@ value)
  def mul(value: Double): BaseOperation = new SimpleOperation("mul", (i: RGB) => i * value)
  def div(value: Double): BaseOperation = new SimpleOperation("div", (i: RGB) => i / value)
  def invDiv(value: Double): BaseOperation = new SimpleOperation("inv div", (i: RGB) => i /@ value)

  def pow(value: Double): BaseOperation = new SimpleOperation("pow", (i: RGB) => i pow value)
  def log(value: Double): BaseOperation = new SimpleOperation("log", (i: RGB) => i log value)
  def min(value: Double): BaseOperation = new SimpleOperation("min", (i: RGB) => i min value)
  def max(value: Double): BaseOperation = new SimpleOperation("max", (i: RGB) => i max value)

  def abs(value: Double = 0): BaseOperation = new SimpleOperation("abs", (i: RGB) => i.abs)
  def greyscale(value: Double = 0): BaseOperation =  new SimpleOperation("greyscale", (i: RGB) => i.toGrey)
  def invert(value: Double = 0): BaseOperation = Operations.invSub(1)

  def fill(rgb: RGB = null): BaseOperation = new FillOperation() // new SimpleOperation("fill", (i: RGB) => rgb)

  def call(name: String)(argument: Double): BaseOperation = name match {
    case "add" => add(argument)
    case "sub" => sub(argument)
    case "inv sub" => invSub(argument)
    case "mul" => mul(argument)
    case "div" => div(argument)
    case "inv div" => invDiv(argument)

    case "pow" => pow(argument)
    case "log" => log(argument)
    case "abs" => abs(argument)
    case "min" => min(argument)
    case "max" => max(argument)

    case "greyscale" => greyscale(argument)
    case "invert" => invert(argument)

    case _ => CompositeDB.findComposite(name)
  }

  def needsArgument(op: Double => BaseOperation): Boolean = needsArgument(op(0).name)

  def needsArgument(name: String): Boolean = name match {
    case "limit" | "abs" | "greyscale" | "invert" | "fill" => true
    case _ => false
  }

  def createComposite(name: String, ops: List[BaseOperation]): Unit = CompositeDB.composites.addOne(new CompositeOperation(name, ops))
}
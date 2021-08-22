package backend.engine

import backend.layers.{Image, RGB}
import javafx.collections.ObservableList
import scalafx.collections.ObservableBuffer
import javafx.scene.paint.Color

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

abstract case class BaseOperation(name: String) {
  def operate(rgb: RGB): RGB

  def inSelection(pixelCoordinates: (Double, Double), r: Selection): Boolean = {
    r.isPixelInside(pixelCoordinates)
//    pixelCoordinates._1 >= r.getX && pixelCoordinates._1 <= r.getX + r.getWidth && pixelCoordinates._2 >= r.getY && pixelCoordinates._2 <= r.getY + r.getHeight
  }
  def isPixelInSelection(pixelCoordinates: (Double, Double), selectionList: List[Selection]): Boolean = {
    @tailrec
    def iterSelections(selection: List[Selection]): Boolean = selection match {
      case s::ss => if (!inSelection(pixelCoordinates, s)) iterSelections(ss) else true
      case Nil => false
    }
    iterSelections(selectionList)
  }

  def apply(image: Image, selection: List[Selection]): Image = {
    val img = image.getBufferedImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if selection.isEmpty || isPixelInSelection(image.actualCoordinates(x, y), selection)) {
      img.setRGB(x, y, operate(img.getRGB(x, y)).limit())
    }
    image.deepCopy()
  }
  override def toString: String = name
}

class SimpleOperation(name: String, operation: RGB => RGB) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = operation(rgb)
}

class FillOperation() extends BaseOperation("fill") {
  implicit def intRGBFromSelectionFill(rectangle: Selection): Int = {
    rectangle.color
  }
  override def apply(image: Image, selection: List[Selection]): Image = {
    val img = image.getBufferedImage
    // iterate only trough selections
    for (rect <- selection;
         x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if inSelection(image.actualCoordinates(x, y), rect)) {
      img.setRGB(x, y, rect)
    }
    image.deepCopy()
  }

  override def operate(rgb: RGB): RGB = rgb
}

// todo test
class BlendOperation() extends BaseOperation("blend") {
  override def operate(rgb: RGB): RGB = rgb

   def apply(image1: Image, image2: Image): Image = {
    val img = image1.getBufferedImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if x < image1.getBufferedImage.getWidth && y < image2.getBufferedImage.getHeight) {
      img.setRGB(x, y, image1.getPixelWithOpacity(x, y) blend image2.getPixelWithOpacity(x, y))
    }
    image1.deepCopy()
  }
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

@SerialVersionUID(102L)
object CompositeDB extends Serializable{
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

  def fill(rgb: RGB = null): BaseOperation = new FillOperation()

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
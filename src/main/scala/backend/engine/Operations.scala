package backend.engine

import backend.layers.{Image, RGB}
import javafx.collections.ObservableList
import scalafx.collections.ObservableBuffer

import scala.collection.mutable.ListBuffer

abstract case class BaseOperation(name: String) {
  def operate(rgb: RGB): RGB
  def apply(image: Image): Image = {
    val img = image.getImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight) {
      img.setRGB(x, y, operate(img.getRGB(x, y)).limit())
    }
    image.deepCopy()
  }
  override def toString: String = name
}

class SimpleOperation(name: String, operation: RGB => RGB) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = operation(rgb)
}

class CompositeOperation(name: String, operations: List[BaseOperation]) extends BaseOperation(name) {
  override def operate(acc: RGB): RGB = operations.foldLeft(acc)((rgb, bo) => bo.operate(rgb))

  override def toString: String = {
    super.toString
    operations.foldLeft("")((s, op) => op.toString + " ")
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

  def call(name: String): Double => BaseOperation = name match {
    case "add" => add
    case "sub" => sub
    case "inv sub" => invSub
    case "mul" => mul
    case "div" => div
    case "inv div" => invDiv

    case "pow" => pow
    case "log" => log
    case "abs" => abs
    case "min" => min
    case "max" => max

    case "greyscale" => greyscale
    case "invert" => invert
    //case _ => CompositeDB.findComposite(name)
  }

  def needsArgument(op: Double => BaseOperation): Boolean = needsArgument(op(0).name)

  def needsArgument(name: String): Boolean = name match {
    case "limit" | "abs" | "greyscale" | "invert" => true
    case _ => false
  }

  def createComposite(name: String, ops: List[BaseOperation]): Unit = CompositeDB.composites.addOne(new CompositeOperation(name, ops))
}
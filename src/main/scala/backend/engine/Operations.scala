package backend.engine

import backend.layers.{Image, RGB}

import scala.collection.mutable.ListBuffer

abstract case class BaseOperation(val name: String) {
  def operate(rgb: RGB): RGB
  def apply(image: Image): Image = {
    val img = image.getImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight) {
      img.setRGB(x, y, operate(img.getRGB(x, y)))
    }
    image.copy()
  }

  override def toString: String = name
}

class SimpleOperation(name: String, operation: RGB => RGB) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = operation(rgb)
}

class CompositeOperation(name: String, operations: List[BaseOperation]) extends BaseOperation(name) {
  override def operate(acc: RGB): RGB = operations.foldLeft(acc)((rgb, bo) => bo.operate(rgb))
}

object CompositeDB {
  val composites: ListBuffer[CompositeOperation] = new ListBuffer[CompositeOperation]
  composites.addOne(new CompositeOperation("test", Nil))
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
  def abs(): BaseOperation = new SimpleOperation("abs", (i: RGB) => i.abs)
  def min(value: Double): BaseOperation = new SimpleOperation("min", (i: RGB) => i min value)
  def max(value: Double): BaseOperation = new SimpleOperation("max", (i: RGB) => i max value)
  def greyscale(): BaseOperation =new SimpleOperation("greyscale", (i: RGB) => (i.toGrey))

  def call(name: String): Double => BaseOperation = name match {
    case "add" => add
    case "sub" => sub
    case "inv sub" => invSub
    case "mul" => mul
    case "inv div" => invDiv
  }
}
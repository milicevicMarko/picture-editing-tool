package backend.engine

import backend.layers.{Image, RGB}

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

case class Operation(op: RGB => RGB) {
  def andThen(that: Operation): Operation = Operation(this.op andThen that.op)
  def apply(image: Image): Image = {
    val img = image.getImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight) {
      img.setRGB(x, y, op(img.getRGB(x, y)))
    }
    image.copy()
  }
}

class CompositeOperation(val name: String, val operations: List[Operation]) {
  def apply(image: Image): Image = operations.foldLeft(image)((img, op) => op.apply(img))

  override def toString: String = name
}

object CompositeDB {
  val composites: ListBuffer[CompositeOperation] = new ListBuffer[CompositeOperation]
  composites.addOne(new CompositeOperation("test", Nil))
}

object Operations {
  implicit def funcToOperation(f: RGB => RGB): Operation = Operation(f)

  def add(value: Double): Operation = (i: RGB) => i + value
  def sub(value: Double): Operation = (i: RGB) => i - value
  def invSub(value: Double): Operation = (i: RGB) => i -@ value
  def mul(value: Double): Operation = (i: RGB) => i * value
  def div(value: Double): Operation = (i: RGB) => i / value
  def invDiv(value: Double): Operation = (i: RGB) => i /@ value

  def pow(value: Double): Operation = (i: RGB) => i pow value
  def log(value: Double): Operation = (i: RGB) => i log value
  def abs(): Operation = (i: RGB) => i.abs
  def min(value: Double): Operation = (i: RGB) => i min value
  def max(value: Double): Operation = (i: RGB) => i max value

  def greyscale(): Operation = (i: RGB) => (i.toGrey)

  def call(name: String): Double => Operation = name match {
    case "add" => add
    case "sub" => sub
    case "inv sub" => invSub
    case "mul" => mul
    case "inv div" => invDiv
  }
}

object a extends App {
  val f: String => Int = s => s.length
  val g: Int => Int = i => i * 2

  val h = f andThen g // h(x) => g(f(x))
  println(h("Marko"))
}
package backend.engine

import backend.layers.{Image, RGB}

import scala.language.implicitConversions

case class Operation(f: RGB => RGB) {
  def andThen(that: Operation): Operation = Operation(this.f andThen that.f)
  def apply(image: Image): Image = {
    val imageCopy = image.copy()
    val img = imageCopy.getImage

    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight) {
      img.setRGB(x, y, f(img.getRGB(x, y)))
    }
    imageCopy
  }
}

class CompositeOperation(val operations: List[Operation]) {
  def apply(image: Image): Image = operations.foldLeft(image)((img, op) => op.apply(img))
}

object Operations {
  implicit def funcToOperation(f: RGB => RGB): Operation = Operation(f)

  def add(value: Double): Operation = (i: RGB) => i + value
  def sub(value: Double): Operation = (i: RGB) => i - value
}

object a extends App {
  val f: String => Int = s => s.length
  val g: Int => Int = i => i * 2

  val h = f andThen g // h(x) => g(f(x))
  println(h("Marko"))


}
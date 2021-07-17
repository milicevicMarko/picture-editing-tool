package backend.layers

import scala.collection.mutable.ListBuffer

object ImageManager {
  val imageBuffer: ListBuffer[Image2] = new ListBuffer[Image2]

  def add(image :Image2): Unit = imageBuffer.addOne(image)

  def swap(image1: Image2, image2: Image2): Unit = ???

  def move(image: Image2, index: Int): Unit = ???

  def remove(image: Image2): Unit = imageBuffer.remove(image.index)
}

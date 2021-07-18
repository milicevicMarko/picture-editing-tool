package backend.layers

import backend.io.FileBrowser

import scala.collection.mutable.ListBuffer

object ImageManager {
  val imageBuffer: ListBuffer[Image] = new ListBuffer[Image]

  var selected: Int = size
  def setSelect(s: Int): Unit = selected = s
  def getSelected: Int = selected

  // todo - this returns top picture for now!
  def getSelectedImage: Image = imageBuffer(size - 1)
  def imageAt(i: Int): Image = imageBuffer(i)

  def size: Int = imageBuffer.size

  def addNewImage(): Unit = FileBrowser.chooseImportPath() match {
    case path: String if path.nonEmpty => add(new Image(path))
    case "" => println("Canceled")
    case _ => println("Unexpected")
  }

  def add(image :Image): Unit = imageBuffer.addOne(image)

  def swap(image1: Image, image2: Image): Unit = {
    imageBuffer.update(image1.index, image2)
    imageBuffer.update(image2.index, image1)

    val ind1 = image1.index
    image1.index = image2.index
    image2.index = ind1
  }

  def move(image: Image, index: Int): Unit = ???

  def remove(image: Image): Unit = imageBuffer.remove(image.index)
}

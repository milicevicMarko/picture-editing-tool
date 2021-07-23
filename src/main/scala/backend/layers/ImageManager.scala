package backend.layers

import scalafx.collections.ObservableBuffer
import scalafx.scene.image.ImageView

object ImageManager {
  val imageBuffer: ObservableBuffer[Image] = new ObservableBuffer[Image]

  def size: Int = imageBuffer.size

  def selected: List[Image] = imageBuffer.filter(image => image.isSelected).toList

  def deselectAll(): Unit = selected.foreach(i => i.select())

  def imageAt(i: Int): Image = imageBuffer(i)

  def allImageViews: List[ImageView] = imageBuffer.map(image => image.imageView).toList

  def add(paths: List[String]): List[Image] = paths.collect(path => add(path))

  def add(path: String): Image = add(new Image(path))

  def add(image :Image): Image = {
    imageBuffer.addOne(image)
    image
  }

  def swap(): Unit = selected match {
    case x::y::xs => swap(x, y)
    case _ => println("Did not select enough")
  }

  def swap(image1: Image, image2: Image): Unit = {
    def updateSingle(image: Image, newIndex: Int): Unit = {
      imageBuffer.update(newIndex, image)
      image.index = newIndex
      image.cardView.updateIndex()
    }
    val index1 = image1.index
    val index2 = image2.index
    updateSingle(image1, index2)
    updateSingle(image2, index1)
    // imageBuffer.onChange()
  }

  def removeImage(image: Image): Unit = imageBuffer.remove(image)

  def rotate(isRight: Boolean): Unit = selected.foreach(img => img rotate isRight)

}

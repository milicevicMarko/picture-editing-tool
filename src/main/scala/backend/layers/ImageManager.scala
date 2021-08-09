package backend.layers

import backend.engine.BaseOperation
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.ImageView

object ImageManager {
  val imageBuffer: ObservableBuffer[Image] = new ObservableBuffer[Image]

  def size: Int = imageBuffer.size

  def selected: List[Image] = imageBuffer.filter(image => image.isSelected).toList

  def deselectAll(): Unit = imageBuffer.foreach(i => i.select(false))
  def selectAll(): Unit = imageBuffer.foreach(i => i.select(true))

  def imageAt(i: Int): Image = imageBuffer(i)

  // distinct must be added for updating list scenario
  def allImageViews: List[ImageView] = imageBuffer.distinct.map(image => image.imageView).toList

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
      image.setIndex(newIndex)
    }
    val index1 = image1.index
    val index2 = image2.index
    updateSingle(image1, index2)
    updateSingle(image2, index1)
  }

  def updateIndexes(): Unit = imageBuffer.foreach(img => img.setIndex(imageBuffer.indexOf(img)))

  def moveUp(image: Image): Unit = if (image.index != 0) swap(image, imageAt(image.index - 1))

  def moveDown(image: Image): Unit = if (image.index != size - 1) swap(image, imageAt(image.index + 1))

  def remove(image: Image): Unit = {
    imageBuffer.remove(image)
    updateIndexes()
  }

  def duplicate(image: Image): Unit = imageBuffer.addOne(image.copy(false))

  def rotate(isRight: Boolean): Unit = selected.foreach(img => img rotate isRight)

  def operate(op: BaseOperation): Unit = selected.foreach(image => imageBuffer.update(image.index, op(image)))

  def usefulDebug(op: BaseOperation): Unit = {
    val rgbIntBefore = imageAt(0).getImage.getRGB(0,0)
    val rgbBefore = RGB.toRGB(rgbIntBefore)
    println(s"Before: $rgbIntBefore, $rgbBefore")
    imageBuffer.update(0, op(imageAt(0)))
    val rgbIntAfter = imageAt(0).getImage.getRGB(0,0)
    val rgbAfter = RGB.toRGB(rgbIntAfter)
    println(s"After: $rgbIntAfter, $rgbAfter")
  }
}

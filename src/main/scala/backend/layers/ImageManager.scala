package backend.layers

import frontend.utils.UIUtils
import scalafx.scene.layout.StackPane

import scala.collection.mutable.ListBuffer

object ImageManager {
  val imageBuffer: ListBuffer[Image2] = new ListBuffer[Image2]

  var selected: Int = -1
  def setSelect(s: Int): Unit = selected = s
  def getSelected: Int = selected
  def size: Int = imageBuffer.size

  def add(image :Image2): Unit = imageBuffer.addOne(image)

  def swap(image1: Image2, image2: Image2): Unit = {
    imageBuffer.update(image1.index, image2)
    imageBuffer.update(image2.index, image1)
  }

  def move(image: Image2, index: Int): Unit = ???

  def remove(image: Image2): Unit = imageBuffer.remove(image.index)

  def update(pane: StackPane): Unit = {
    pane.children.clear()
    for (img <- imageBuffer) yield {
      UIUtils.fixCenterFit(img.imageView, pane)
      pane.children.add(img.imageView)
    }
  }
}

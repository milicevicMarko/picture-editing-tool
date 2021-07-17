package backend.layers

import backend.io.FileBrowser
import frontend.utils.UIUtils
import scalafx.scene.layout.StackPane
import scalafx.stage.FileChooser

import scala.collection.mutable.ListBuffer

object ImageManager {
  val imageBuffer: ListBuffer[Image] = new ListBuffer[Image]

  var selected: Int = -1
  def setSelect(s: Int): Unit = selected = s
  def getSelected: Int = selected
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
  }

  def move(image: Image, index: Int): Unit = ???

  def remove(image: Image): Unit = imageBuffer.remove(image.index)

  def update(pane: StackPane): Unit = {
    pane.children.clear()
    for (img <- imageBuffer) yield {
      UIUtils.fixCenterFit(img.imageView, pane)
      pane.children.add(img.imageView)
    }
  }
}

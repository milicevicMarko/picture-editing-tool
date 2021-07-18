package backend.layers

import backend.io.FileBrowser

import scala.annotation.tailrec
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

  def addNewImage(): Option[Image] = FileBrowser.chooseImportPath() match {
    case path: String if path.nonEmpty =>
      val img = new Image(path)
      add(img)
      Some(img)
    case _ => None
  }

  def add(image :Image): Unit = imageBuffer.addOne(image)

  def swap(image1: Image, image2: Image): Unit = {
    imageBuffer.update(image1.index, image2)
    imageBuffer.update(image2.index, image1)

    val ind1 = image1.index
    image1.index = image2.index
    image2.index = ind1
  }

  def toTop(image: Image): Unit = move(image, 0)

  def move(image: Image, index: Int): Unit = {
    @tailrec
    def updateList(image: Image, acc: ListBuffer[Image], remainingList: ListBuffer[Image], index: Int): ListBuffer[Image] = {
      if (index == 0) {
        acc.addOne(image).addAll(remainingList)
        // todo update all indexes, easiest trough a for
      } else {
        // todo error
        updateList(image, acc.addOne(remainingList.head), remainingList.tail, index - 1)
      }

    }
    //    imageBuffer.clear()
    // when to clear
    imageBuffer.addAll(updateList(image, new ListBuffer[Image], imageBuffer, index))
  }

  def remove(image: Image): Unit = imageBuffer.remove(image.index)
}

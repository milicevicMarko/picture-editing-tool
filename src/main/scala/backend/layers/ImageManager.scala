package backend.layers

import backend.io.FileBrowser
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.ImageView

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object ImageManager {
  val imageBuffer: ObservableBuffer[Image] = new ObservableBuffer[Image]
  var selected: List[Image] = Nil

  def setSelected(selected: List[Image]): Unit = this.selected = selected
  def getSelected: List[Image] = selected

  // todo - this returns top picture for now!
  def getSelectedImage: Image = imageBuffer(size - 1)
  def imageAt(i: Int): Image = imageBuffer(i)

  def allImageViews: List[ImageView] = for (img <- imageBuffer.toList) yield img.imageView

  def size: Int = imageBuffer.size

  def addNewImage(): Option[Image] = FileBrowser.chooseImportPath() match {
    case path: String if path.nonEmpty =>
      val img = new Image(path)
      add(img)
      Some(img)
    case _ => None
  }

  def add(paths: List[String]): List[Image] = paths.map(path => add(path))

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
  }

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
  }

  def remove(image: Image): Unit = imageBuffer.remove(image.index)

  def rotate(isRight: Boolean): Unit = {
    selected.foreach(img => img.rotateImage(isRight))
  }

}

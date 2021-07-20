package backend.layers

import backend.io.FileBrowser
import frontend.layers.CardView
import javafx.collections.{FXCollections, ObservableList}
import scalafx.scene.image.ImageView

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object ImageManager {
  val imageBuffer: ListBuffer[Image] = new ListBuffer[Image]
  val observableList: ObservableList[CardView] = FXCollections.observableArrayList()
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
    observableList.add(new CardView(image))
    image
  }

  def swap(): Unit = selected match {
    case x::y::xs => swap(x, y)
    case _ => println("Did not select enough")
  }

  def swap(image1: Image, image2: Image): Unit = {
    imageBuffer.update(image1.index, image2)
    imageBuffer.update(image2.index, image1)

    val ind1 = image1.index
    image1.index = image2.index
    image2.index = ind1

    // todo layer list swap
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

  def rotate(isRight: Boolean): Unit = {
    selected.foreach(img => img.rotateImage(isRight))
  }

}

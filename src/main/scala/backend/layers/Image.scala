package backend.layers

import backend.io.FileImport
import frontend.layers.CardView
import frontend.utils.UIUtils
import scalafx.scene.image.ImageView
import scalafx.scene.layout.Pane

import java.awt.image.BufferedImage
import java.io.File

// todo create imageview Center and Thumbnail classes
class Image(bufferedImage: BufferedImage, path: String = "", var index: Int = ImageManager.size) { //} extends Ordered[Image] {
  def this(path: String) = this(FileImport.loadImage(path), path)
  def copy(): Image = new Image(bufferedImage, path, index) // todo name should point to copy -> such as duplicate_copy.jpg

  def getImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getPath: String = path
  val name: String = new File(path).getName

  lazy val cardView: CardView = new CardView(this)
  lazy val imageView: ImageView = UIUtils.imageToImageView(bufferedImage)

  var isSelected: Boolean = false
  def select(): Unit = isSelected = !isSelected
  def select(setSelect: Boolean): Unit = isSelected = setSelect

  def setOpacity(v: Double): Unit = imageView.setOpacity(v)

  def rotate(isRight: Boolean): Unit = {
    val degrees = if (isRight) 90 else -90
    imageView.setRotate(imageView.getRotate + degrees)
  }

  def bindTo(pane: Pane): Image = {
    imageView.fitWidthProperty().bind(pane.widthProperty().subtract(100))
    imageView.fitHeightProperty().bind(pane.heightProperty().subtract(100))
    imageView.setPreserveRatio(true)
    this
  }
}

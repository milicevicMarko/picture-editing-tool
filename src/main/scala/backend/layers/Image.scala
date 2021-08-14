package backend.layers

import backend.io.FileImport
import frontend.layers.CardView
import frontend.utils.UIUtils
import scalafx.scene.image.ImageView
import scalafx.scene.layout.Pane

import java.awt.image.BufferedImage
import java.io.File

class Image(bufferedImage: BufferedImage, path: String = "", var index: Int = ImageManager.size) {
  def this(path: String) = this(FileImport.loadImage(path), path)
  def this(path: String, index: Int) = this(FileImport.loadImage(path), path, index)

  def copy(sameIndex: Boolean = true): Image = if (sameIndex) new Image(path, index) else new Image(path)
  def deepCopy() = new Image(bufferedImage, path, index)

  def setIndex(index: Int): Unit = {
    this.index = index
    cardView.updateIndex()
  }

  def refresh(): Unit = ImageManager.imageBuffer.update(index, new Image(bufferedImage, path, index))
  def getImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getPath: String = path
  val name: String = new File(path).getName

  lazy val cardView: CardView = new CardView(this)
  lazy val imageView: ImageView = UIUtils.imageToImageView(bufferedImage) // check if you can use def

  var isActive: Boolean = true
  def activate(): Unit = isActive = !isActive
  def activate(setActive: Boolean): Unit = isActive = setActive

  var isSelected: Boolean = false
  def select(): Unit = isSelected = !isSelected
  def select(setSelect: Boolean): Unit = isSelected = setSelect

  def setOpacity(v: Double): Unit = imageView.setOpacity(v)
  def getOpacity: Double = imageView.getOpacity

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

  def getPixel(x: Int, y: Int): RGB = getImage.getRGB(x, y)
  def getPixelWithOpacity(x: Int, y: Int): RGB = getPixel(x, y) * getOpacity

  def blend(that: Image): Image = {
    val img = getImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if x < that.getImage.getWidth && y < that.getImage.getHeight) {
      img.setRGB(x, y, this.getPixelWithOpacity(x, y) blend that.getPixelWithOpacity(x, y))
    }
    deepCopy()
  }
}

object Image {
  def emptyImage(image: Image): Image = emptyImage(image.getImage.getWidth, image.getImage.getHeight)
  def emptyImage(width: Int, height: Int): Image = new Image(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))
}

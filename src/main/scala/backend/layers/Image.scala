package backend.layers

import backend.io.FileImport
import frontend.layers.CardView
import frontend.utils.UIUtils
import scalafx.scene.image.ImageView
import scalafx.scene.layout.Pane

import java.awt.image.BufferedImage
import java.io.File

// todo create imageview Center and Thumbnail classes
class Image(bufferedImage: BufferedImage, path: String = "", var index: Int = ImageManager.size) {
  def this(path: String) = this(FileImport.loadImage(path), path)
  def this(path: String, index: Int) = this(FileImport.loadImage(path), path, index)
  def copy(useSameIndex: Boolean = true): Image = if (useSameIndex) new Image(path, index) else new Image(path)

  def setIndex(index: Int): Unit = {
    this.index = index
    cardView.updateIndex()
  }

  def getImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getPath: String = path
  val name: String = new File(path).getName

  lazy val cardView: CardView = new CardView(this)
  lazy val imageView: ImageView = UIUtils.imageToImageView(bufferedImage)

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

  def getPixel(x: Int, y: Int, withOpacity: Boolean): RGB = {
//    val pixel: RGB = getPixel(x, y).withOpacity(getOpacity)
    val pixel: RGB = getPixel(x, y)
    val r = pixel * getOpacity
    println(s"(x,y)_($x, $y)   pixel: ${pixel} vs r: ${r}")
    r
  }

  def blend(that: Image): Image = {
    val newImage = this.copy()
    val img = newImage.getImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight;
         if x < that.getImage.getWidth && y < that.getImage.getHeight) {
      val pixel: RGB =  this.getPixel(x, y, true) blend that.getPixel(x, y, true)
      img.setRGB(x, y, pixel)
      println(pixel)
    }
    newImage
  }
}

package backend.layers

import backend.io.FileImport
import frontend.utils.UIUtils
import scalafx.scene.image.ImageView

import java.awt.image.BufferedImage
import java.io.File

class Image(bufferedImage: BufferedImage, path: String = "", var index: Int = ImageManager.size) {
  def this(path: String) = this(FileImport.loadImage(path), path)

  def getImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getPath: String = path
  def getName: String = new File(path).getName

  def x: Int = getImage.getMinX
  def y: Int = getImage.getMinY
  def width: Int = getImage.getWidth
  def height: Int = getImage.getHeight
  val imageView: ImageView = UIUtils.imageToImageView(bufferedImage)
  val thumbnail: ImageView = UIUtils.imageToThumbnail(bufferedImage)

  def setOpacity(v: Double): Unit = imageView.setOpacity(v)
}

// todo create imageview Center and Thumbnail classes

object Image {
  def rotateImage(image: Image, isRight: Boolean): Unit = {
    val degrees = if (isRight) 90 else -90
    image.imageView.setRotate(image.imageView.getRotate + degrees)
  }
}
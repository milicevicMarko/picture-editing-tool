package backend.layers

import backend.io.FileImport
import frontend.utils.UIUtils
import scalafx.scene.image.ImageView

import java.awt.image.BufferedImage

class Image2 (bufferedImage: BufferedImage, path: String = "", var index: Int = 0) {
  def getImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getPath: String = path

  def x: Int = getImage.getMinX
  def y: Int = getImage.getMinY
  def width: Int = getImage.getWidth
  def height: Int = getImage.getHeight
  def imageView: ImageView = UIUtils.createImageViewFromImage(bufferedImage)
  def resize: ImageView = ???
  def setOpacity(v: Double): Unit = imageView.setOpacity(v)
}

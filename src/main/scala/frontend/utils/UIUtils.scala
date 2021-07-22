package frontend.utils

import scalafx.scene.image.{ImageView, WritableImage}

import java.awt.image.BufferedImage

object UIUtils {

  def imageToThumbnail(bi: BufferedImage): ImageView = setThumbnailFit()(imageToImageView(bi))

  def setThumbnailFit(width: Int = 100, height: Int = 100, keepRatio: Boolean = true)(imageView: ImageView): ImageView = {
    imageView.setFitWidth(width)
    imageView.setFitHeight(height)
    imageView.setPreserveRatio(keepRatio)
    imageView
  }

  def imageToImageView(img: BufferedImage): ImageView = {
    val wrImg: WritableImage = new WritableImage(img.getWidth, img.getHeight)
    val pxImg = wrImg.getPixelWriter
    for (x <- 0 until img.getWidth)
      for (y <- 0 until img.getHeight)
        pxImg.setArgb(x, y, img.getRGB(x, y))
    new ImageView(wrImg)
  }
}

object m {
  def imageBind(pane: Int)(image: Int): Unit = {
    println(s"$pane and $image")
  }

  def main(args: Array[String]): Unit = {
    def f: Int => Unit = imageBind(5)
    f(7)
  }
}
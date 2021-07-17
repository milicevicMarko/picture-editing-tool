package frontend.utils

import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.layout.Pane

import java.awt.image.BufferedImage

object UIUtils {
  def createImageViewFromImage(img: BufferedImage): ImageView = {
    val imageView: ImageView = new ImageView()
    UIUtils.convertImageToImageView(img, imageView)
    imageView
  }

  def createThumbnailImageViewFromImage(img: BufferedImage): ImageView = fixThumbnailFit(createImageViewFromImage(img))


  def fixThumbnailFit(imageView: ImageView, width: Int = 100, height: Int = 100, keepRatio: Boolean = true): ImageView = {
    imageView.setFitWidth(width)
    imageView.setFitHeight(height)
    imageView.setPreserveRatio(keepRatio)
    imageView
  }

  def convertImageToImageView(img: BufferedImage, imageView: ImageView): Unit = {
    val wrImg: WritableImage = new WritableImage(img.getWidth, img.getHeight)
    val pxImg = wrImg.getPixelWriter
    for (x <- 0 until img.getWidth)
      for (y <- 0 until img.getHeight)
        pxImg.setArgb(x, y, img.getRGB(x, y))
    imageView.setImage(new ImageView(wrImg).getImage)
  }

  def fixCenterFit(imgView: ImageView, pane: Pane, keepRatio: Boolean = true): Unit = {
    imgView.fitWidthProperty().bind(pane.widthProperty().subtract(100))
    imgView.fitHeightProperty().bind(pane.heightProperty().subtract(100))
    imgView.setPreserveRatio(keepRatio)
  }
}

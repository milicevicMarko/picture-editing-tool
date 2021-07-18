package frontend.utils

import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.layout.Pane

import java.awt.image.BufferedImage

//class CenterImage extends ImageView {
//  def this(writableImage: WritableImage) = {
//    this()
//    new ImageView(writableImage)
//  }
//  def this(bi: BufferedImage) = {
//    val wrImg: WritableImage = new WritableImage(bi.getWidth, bi.getHeight)
//    val pxImg = wrImg.getPixelWriter
//    for (x <- 0 until bi.getWidth)
//      for (y <- 0 until bi.getHeight)
//        pxImg.setArgb(x, y, bi.getRGB(x, y))
//    this(wrImg)
//  }
//}

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

  def bindImageViewToPane(imgView: ImageView)(pane: Pane, keepRatio: Boolean = true): Unit = {
    imgView.fitWidthProperty().bind(pane.widthProperty().subtract(100))
    imgView.fitHeightProperty().bind(pane.heightProperty().subtract(100))
    imgView.setPreserveRatio(keepRatio)
  }
}

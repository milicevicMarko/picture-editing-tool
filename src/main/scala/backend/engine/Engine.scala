package backend.engine

import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.layout.StackPane

import java.awt.image.BufferedImage
import java.io.File

object Engine {
  var image: Option[BufferedImage] = None
  var imageFile: Option[File] = None

  def setImageFile(file: File): Unit = imageFile = Some(file)
  def getImageFile: File = imageFile.getOrElse(throw new IllegalStateException())

  def setImage(image: BufferedImage): Unit = this.image = Option(image)
  def getImage: BufferedImage = this.image.getOrElse(throw new IllegalStateException())
  def getImageOption: Option[BufferedImage] = this.image

  def pictureTest(): Unit = {
    println("Started test")
    val img: BufferedImage = image.get
    val w = img.getWidth
    val h = img.getHeight

    // create new image of the same size
    val out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)

    // copy pixels (mirror horizontally)
    for (x <- 0 until w)
      for (y <- 0 until h)
        out.setRGB(x, y, img.getRGB(w - x - 1, y) & 0xffffff)

    // draw red diagonal line
    for (x <- 0 until (h min w))
      out.setRGB(x, x, 0xff0000)

    setImage(out)
    println("Finished test")
  }

  def rotateImage(imageView: ImageView, isRight: Boolean): Unit = {
    val degrees = if (isRight) 90 else -90
    imageView.setRotate(imageView.getRotate + degrees)
  }

  def updateImage(imageView: ImageView, pane: StackPane): Unit = {
    updateSize(imageView, pane)
    convertImageToFx(imageView)
  }

  def updateSize(imageView: ImageView, pane: StackPane): Unit = {
    val img = image.getOrElse(throw new IllegalArgumentException)
    imageView.setPreserveRatio(true)
    if (img.getWidth() >= img.getHeight()) imageView.setFitWidth(pane.getWidth - 100)
    if (img.getWidth() <= img.getHeight()) imageView.setFitHeight(pane.getHeight - 100)
  }

  def convertImageToFx(imageView: ImageView): Unit = {
    val img = image.get
    val wrImg: WritableImage = new WritableImage(img.getWidth, img.getHeight)
    val pxImg = wrImg.getPixelWriter
    for (x <- 0 until img.getWidth)
      for (y <- 0 until img.getHeight)
        pxImg.setArgb(x, y, img.getRGB(x, y))
    imageView.setImage(new ImageView(wrImg).getImage)
  }

}

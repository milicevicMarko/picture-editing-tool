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

  def getImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getPath: String = path
  val name: String = new File(path).getName
  lazy val cardView: CardView = new CardView(this)

  def x: Int = getImage.getMinX
  def y: Int = getImage.getMinY
  def width: Int = getImage.getWidth
  def height: Int = getImage.getHeight

  // todo move thumnnail to Cardview only?
  // todo remove imageView from class, no need to store the view?
  val imageView: ImageView = UIUtils.imageToImageView(bufferedImage)
  val thumbnail: ImageView = UIUtils.imageToThumbnail(bufferedImage)

  def setOpacity(v: Double): Unit = imageView.setOpacity(v)

  def rotateImage(isRight: Boolean): Unit = {
    val degrees = if (isRight) 90 else -90
    imageView.setRotate(imageView.getRotate + degrees)
  }

  def bind(pane: Pane): Image = {
    imageView.fitWidthProperty().bind(pane.widthProperty().subtract(100))
    imageView.fitHeightProperty().bind(pane.heightProperty().subtract(100))
    imageView.setPreserveRatio(true)
    this
  }

  def addToPane(pane: Pane): Image = {
    pane.children addOne (this bind pane).imageView
    this
  }
}

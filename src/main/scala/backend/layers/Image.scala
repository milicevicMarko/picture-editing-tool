package backend.layers

import backend.io.FileImport
import frontend.layers.CardView
import frontend.utils.UIUtils
import scalafx.scene.image.ImageView
import scalafx.scene.layout.Pane

import java.awt.image.{BufferedImage, WritableRaster, ColorModel}
import java.io.File

class Image(bufferedImage: BufferedImage, path: String = "", var index: Int = ImageManager.size) {
  def this(path: String) = this(FileImport.loadImage(path), path)
  def this(path: String, index: Int) = this(FileImport.loadImage(path), path, index)

  def duplicate(sameIndex: Boolean = true): Image = new Image(copyBufferedImage(), path, if (sameIndex) index else ImageManager.size)
  def deepCopy() = new Image(bufferedImage, path, index)

  def copyBufferedImage(): BufferedImage = {
    def deepCopy(bi: BufferedImage) = {
      val cm = bi.getColorModel
      val isAlphaPremultiplied = cm.isAlphaPremultiplied
      val raster = bi.copyData(null)
      new BufferedImage(cm, raster, isAlphaPremultiplied, null)
    }
    deepCopy(getBufferedImage)
  }

  def setIndex(index: Int): Unit = {
    this.index = index
    cardView.updateIndex()
  }

  def refresh(): Unit = ImageManager.imageBuffer.update(index, new Image(bufferedImage, path, index))
  def getBufferedImage: BufferedImage = if (bufferedImage == null) FileImport.loadImage(path) else bufferedImage
  def getImageView: ImageView = imageView
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
    if (bufferedImage.getWidth > pane.getWidth || bufferedImage.getHeight > pane.getHeight) {
      imageView.fitWidthProperty().bind(pane.widthProperty().subtract(100))
      imageView.fitHeightProperty().bind(pane.heightProperty().subtract(100))
      imageView.setPreserveRatio(true)
    }
    this
  }

  def getPixel(x: Int, y: Int): RGB = getBufferedImage.getRGB(x, y)
  def getPixelWithOpacity(x: Int, y: Int): RGB = getPixel(x, y) * getOpacity

  // todo should this be an op?
  def blend(that: Image): Image = {
    val img = getBufferedImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if x < that.getBufferedImage.getWidth && y < that.getBufferedImage.getHeight) {
      img.setRGB(x, y, this.getPixelWithOpacity(x, y) blend that.getPixelWithOpacity(x, y))
    }
    deepCopy()
  }

  def actualOffsetX: Double = imageView.getLayoutX
  def actualOffsetY: Double = imageView.getLayoutY

  def actualWidth: Double = imageView.getBoundsInParent.getWidth
  def actualHeight: Double = imageView.getBoundsInParent.getHeight

  def relativeWidth: Double = getBufferedImage.getWidth
  def relativeHeight: Double = getBufferedImage.getHeight

  def actualX(relX: Double): Double = relX * actualWidth / relativeWidth + actualOffsetX
  def actualY(relY: Double): Double = relY * actualHeight / relativeHeight +  actualOffsetY
  def actualCoordinates(x: Double, y: Double): (Double, Double) = (actualX(x), actualY(y))

  def debugPositions: (Double, Double) = {
    def center: (Double, Double) = (bufferedImage.getWidth/2, bufferedImage.getHeight/2)

    def prints(name: String, list: List[Any]): Unit = println(list.foldLeft("["+name+"]:\t")((s, l) => s + "\t" + l.toString))
    prints("x,y", List(imageView.getLayoutX, imageView.getLayoutY))
    prints("iv wh 0-max", List(imageView.getX, imageView.getFitWidth,"|", imageView.getY, imageView.getFitHeight))
    prints("bi x,y", List(bufferedImage.getWidth, bufferedImage.getHeight))
    prints("center", List(center._1, center._2, (center._1 + imageView.getLayoutX), (center._2 + imageView.getLayoutY)))
    prints("actual 100, 100", List(actualX(100), actualY(100)))
    (2, 3)
  }
}

object Image {
  def emptyImage(image: Image): Image = emptyImage(image.getBufferedImage.getWidth, image.getBufferedImage.getHeight)
  def emptyImage(width: Int, height: Int): Image = new Image(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))
}

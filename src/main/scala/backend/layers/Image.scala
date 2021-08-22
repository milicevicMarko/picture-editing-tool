package backend.layers

import backend.io.FileImport
import frontend.layers.CardView
import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.layout.Pane

import java.awt.image.BufferedImage
import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import javax.imageio.ImageIO

@SerialVersionUID(101L)
class Image(@transient val bufferedImage: BufferedImage, path: String = "", var index: Int = ImageManager.size) extends Serializable {
  def this(path: String) = this(FileImport.loadImage(path), path)
  def this(path: String, index: Int) = this(FileImport.loadImage(path), path, index)

  // todo serialize name, path, isActivated?
  def duplicate(sameIndex: Boolean = true): Image = new Image(copyBufferedImage(), path, if (sameIndex) index else ImageManager.size)
  def deepCopy() = new Image(bufferedImage, path, index)

  def copyBufferedImage(): BufferedImage = {
    def deepCopy(bi: BufferedImage): BufferedImage = {
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

  // todo could lead to stack overflow if file does not exist!
  def getBufferedImage: BufferedImage = {
    if (bufferedImage == null) {
      val newImage = new Image(FileImport.loadImage(path), path, index)
      ImageManager.imageBuffer.update(index, newImage)
      newImage.getBufferedImage
    } else {
      bufferedImage
    }
  }
  def getImageView: ImageView = imageView
  def getPath: String = path
  val name: String = new File(path).getName
  val tempName: String = index.toString + ".temp"

  @transient lazy val cardView: CardView = new CardView(this)
  @transient lazy val imageView: ImageView = Image.imageToImageView(getBufferedImage) // check if you can use def

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
    if (getBufferedImage.getWidth > pane.getWidth || getBufferedImage.getHeight > pane.getHeight) {
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

  def writeTemporary(): Boolean = ImageIO.write(getBufferedImage, "png", new File(path))
  def overwrite(): Unit = ImageIO.write(getBufferedImage, "png", new File(path))

  def write(outStream: ObjectOutputStream, fileFormat: String = "png"): Boolean = ImageIO.write(getBufferedImage, fileFormat, outStream)

  override def toString: String = path
}

object Image {
  def read(inFile: File): Image = {
    val is = new ObjectInputStream(new FileInputStream(inFile))
    val img = Image.read(is)
    is.close()
    img
  }
  def read(is: ObjectInputStream): Image = new Image(ImageIO.read(is))

  def emptyImage(image: Image): Image = emptyImage(image.getBufferedImage.getWidth, image.getBufferedImage.getHeight)
  def emptyImage(width: Int, height: Int): Image = new Image(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))

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
    for (x <- 0 until img.getWidth;
        y <- 0 until img.getHeight) {
      pxImg.setArgb(x, y, img.getRGB(x, y))
    }
    new ImageView(wrImg)
  }
}

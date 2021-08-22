package backend.engine

import backend.engine.Selection.transparentRGB
import backend.layers.RGB
import javafx.scene.paint.Color
import scalafx.Includes._
import scalafx.scene.shape.Rectangle

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

@SerialVersionUID(105L)
class Selection(val x: Double, val y: Double, val width: Double = 0, val height: Double = 0, val color: RGB = transparentRGB) extends Serializable {
  def this (x: Double, y: Double, w: Double, h: Double, c: Color) = this(x, y, w, h, RGB.toRGB(c))

  def xBorder: (Double, Double) = (x, x + width)
  def yBorder: (Double, Double) = (y, y + height)
  def fill(color: Color): Selection = new Selection(x, y, width, height, RGB.toRGB(color))
  def toRectangle: Rectangle = Selection.toRectangle(this)
  def isTransparent: Boolean = color == transparentRGB

  def isPixelInside(pixelCoordinates: (Double, Double)): Boolean =
    pixelCoordinates._1 >= xBorder._1 && pixelCoordinates._1 <= xBorder._2 && pixelCoordinates._2 >= yBorder._1 && pixelCoordinates._2 <= yBorder._2

  override def toString: String = s"Selection: $x, $y, $width, $height, ${color.toString}"

}

object Selection {
  val transparentRGB: RGB = new RGB(0, 0, 0)

  def rectangleEdges(rect: Rectangle): Unit = {
    if (rect.getFill != Color.TRANSPARENT) {
      rect.stroke = Color.BLACK
      rect.strokeWidth = 0.5
      rect.strokeDashArray = Seq(2d)
    } else {
      rect.strokeWidth = 0
    }
  }

  def toRectangle(selection: Selection): Rectangle = new Rectangle() {
    x = selection.x
    y = selection.y
    width = selection.width
    height = selection.height
    fill = selection.color
    rectangleEdges(this)
  }


  def toSelection(rectangle: Rectangle): Selection = {
    if (rectangle.getFill == null)
      rectangle.setFill(Color.TRANSPARENT)
    new Selection(rectangle.getX, rectangle.getY, rectangle.getWidth, rectangle.getHeight, rectangle.getFill.asInstanceOf[Color])
  }

}

object SelectionManager extends Serializable {
  val buffer: ListBuffer[Selection] = new ListBuffer[Selection]
  val storageReference: String = "dataSelection.temp"
  def last: Selection = buffer.last

  def isRectangleColored(rectangle: Rectangle): Boolean = rectangle.getFill != null && rectangle.getFill != Color.TRANSPARENT

  def add(rect: Rectangle): Unit = add(Selection.toSelection(rect))

  def add(selection: Selection): Unit = buffer.addOne(selection)

  def add(list: List[Selection]): Unit = buffer.addAll(list)

  def test(): Unit = {
    val r = new Rectangle() {
      x = 100
      y = 100
      width = 100
      height = 100
    }
    add(r)
    add(r)

    write()
  }

  def write(): Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream(storageReference))
    oos.writeObject(buffer.toList)
    oos.close()
  }

  def read(): Unit = {
    val ois = new ObjectInputStream(new FileInputStream(storageReference))
    val a = ois.readObject().asInstanceOf[List[Selection]]
    println(a)
    ois.close()
  }
}

package backend.engine

import backend.layers.RGB
import javafx.scene.paint.Color
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.shape.Rectangle

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.language.{implicitConversions, reflectiveCalls}

@SerialVersionUID(105L)
class Selection(val x: Double, val y: Double, val width: Double = 0, val height: Double = 0, @transient val rectangle: Rectangle, val isTransparent: Boolean = true, val color: RGB = new RGB(0, 0, 0)) extends Serializable {
  def this (x: Double, y: Double, w: Double, h: Double, rectangle: Rectangle, c: Color) = this(x, y, w, h, rectangle, false, RGB.toRGB(c))

  def xBorder: (Double, Double) = (x, x + width)
  def yBorder: (Double, Double) = (y, y + height)
  def fill(color: Color): Selection = {
    rectangle.fill = color
    new Selection(x, y, width, height, rectangle, false, RGB.toRGB(color))
  }

  def isPixelInside(pixelCoordinates: (Double, Double)): Boolean =
    pixelCoordinates._1 >= xBorder._1 && pixelCoordinates._1 <= xBorder._2 && pixelCoordinates._2 >= yBorder._1 && pixelCoordinates._2 <= yBorder._2

  override def toString: String = s"Selection: $x, $y, $width, $height, ${color.toString}"
}

case class TransparentSelection(xx: Double, yy: Double, ww: Double, hh: Double, rr: Rectangle) extends Selection(xx, yy, ww, hh, rr, true, RGB.toRGB(Color.TRANSPARENT))
case class FilledSelection(xx: Double, yy: Double, ww: Double, hh: Double, rr: Rectangle, rgb: RGB) extends Selection(xx, yy, ww, hh, rr, false, rgb)

object Selection {
  def rectangleEdges(rect: Rectangle): Unit = {
    if (rect.getFill == Color.TRANSPARENT) {
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
    if (!selection.isTransparent)
      fill = selection.color
    rectangleEdges(this)
  }


  def toSelection(rectangle: Rectangle): Selection = {
    if (isRectangleTransparent(rectangle))
      TransparentSelection(rectangle.getX, rectangle.getY, rectangle.getWidth, rectangle.getHeight, rectangle)
    else
      FilledSelection(rectangle.getX, rectangle.getY, rectangle.getWidth, rectangle.getHeight, rectangle, RGB.toRGB(rectangle.getFill.asInstanceOf[Color]))
  }

  def isRectangleTransparent(rectangle: Rectangle): Boolean = rectangle.getFill == null || rectangle.getFill == Color.TRANSPARENT
}

object SelectionManager extends Serializable {
  val buffer: ObservableBuffer[Selection] = new ObservableBuffer[Selection]
  val storageReference: String = "dataSelection.temp"
  def last: Selection = buffer.last
  def size: Int = buffer.size
  def clear(): Unit = buffer.clear()

  def getFilled: List[Selection] = buffer.filter(s => !s.isTransparent).toList
  def hasFilled: Boolean = getFilled.nonEmpty

  def isRectangleColored(rectangle: Rectangle): Boolean = rectangle.getFill != null && rectangle.getFill != Color.TRANSPARENT

  def add(rect: Rectangle): Unit = add(Selection.toSelection(rect))

  def add(selection: Selection): Unit = buffer.addOne(selection)

  def add(list: List[Selection]): Unit = buffer.addAll(list)

  def remove(selection: Selection): Unit = buffer.remove(selection)
  def remove(rectangle: Rectangle): Unit = remove(get(rectangle))

  def get(rectangle: Rectangle): Selection = buffer.find(r => r.rectangle == rectangle).get

  def replace(selection1: Selection, selection2: Selection): Unit = buffer.update(buffer.indexOf(selection1), selection2)

  def newRectangle(xx: Double, yy: Double, ww: Double = 0, hh: Double = 0): Rectangle = new Rectangle() {
    visible = true
    stroke = Color.BLACK
    strokeDashArray = Seq(2d)
    fill = Color.TRANSPARENT
    strokeWidth = 0.5
    x = xx
    y = yy
    width = ww
    height = hh
  }



  def write(): Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream(storageReference))
    oos.writeObject(buffer.toList)
    oos.close()
  }

  def read(): Unit = {
    val ois = new ObjectInputStream(new FileInputStream(storageReference))
    val list = ois.readObject().asInstanceOf[List[Selection]]
    list.foreach(selection => selection.rectangle = Selection.toRectangle(selection))
    ois.close()
    buffer.clear()
    add(list)
  }
}

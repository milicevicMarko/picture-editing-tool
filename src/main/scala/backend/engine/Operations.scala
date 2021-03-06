package backend.engine

import backend.layers.{Image, RGB}
import scalafx.collections.ObservableBuffer

import java.awt.image.BufferedImage
import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.annotation.tailrec
import scala.language.implicitConversions

@SerialVersionUID(107L)
abstract case class BaseOperation(name: String) extends Serializable {
  def operate(rgb: RGB): RGB

  def inSelection(pixelCoordinates: (Double, Double), r: Selection): Boolean = r.isPixelInside(pixelCoordinates)

  def isPixelInSelection(pixelCoordinates: (Double, Double), selectionList: List[Selection]): Boolean = {
    @tailrec
    def iterSelections(selection: List[Selection]): Boolean = selection match {
      case s::ss => if (!inSelection(pixelCoordinates, s)) iterSelections(ss) else true
      case Nil => false
    }
    iterSelections(selectionList)
  }

  def apply(image: Image, selection: List[Selection]): Image = {
    val img = image.getBufferedImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if selection.isEmpty || isPixelInSelection(image.actualCoordinates(x, y), selection)) {
      img.setRGB(x, y, operate(img.getRGB(x, y)).limit())
    }
    image.deepCopy()
  }
  override def toString: String = name
}

class SimpleOperation(name: String, operation: RGB => RGB) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = operation(rgb)
  override def toString: String = operation.toString()
}

class FillOperation() extends BaseOperation("fill") {
  implicit def intRGBFromSelectionFill(rectangle: Selection): Int = {
    rectangle.color
  }
  override def apply(image: Image, selections: List[Selection]): Image = {
    val img = image.getBufferedImage
    // iterate only trough selections
    for (selection <- selections;
         x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if inSelection(image.actualCoordinates(x, y), selection)) {
      img.setRGB(x, y, selection)
    }
    image.deepCopy()
  }

  override def operate(rgb: RGB): RGB = rgb
}

// todo test
class BlendOperation() extends BaseOperation("blend") {
  override def operate(rgb: RGB): RGB = rgb

   def apply(image1: Image, image2: Image): Image = {
    val img = image1.getBufferedImage
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if x < image1.getBufferedImage.getWidth && y < image2.getBufferedImage.getHeight) {
      img.setRGB(x, y, image1.getPixelWithOpacity(x, y) blend image2.getPixelWithOpacity(x, y))
    }
    image1.deepCopy()
  }
}

// todo doesnt work on big images
class CropOperation() extends BaseOperation("crop") {
  override def operate(rgb: RGB): RGB = rgb

  override def apply(image: Image, selections: List[Selection]): Image = {
    val selection = selections.head
    val img = image.getBufferedImage
    @tailrec
    def getStart(x: Int, y: Int): (Int, Int) = {
      if (inSelection(image.actualCoordinates(x, y), selection))
        (x, y)
      else if (x < img.getWidth)
        getStart(x + 1, y)
      else
        getStart(0, y + 1)
    }
    val start = getStart(0, 0)
    val width = if (start._1 + selection.width.toInt > img.getWidth) img.getWidth - start._1 else selection.width.toInt
    val height = if (start._2 + selection.height.toInt > img.getHeight) img.getHeight - start._2 else selection.height.toInt

    SelectionManager.buffer.clear()
    new Image(img.getSubimage(start._1, start._2, width, height), image.getPath, image.index)
  }
}

class CompositeOperation(name: String, operations: List[BaseOperation]) extends BaseOperation(name) {
  override def operate(acc: RGB): RGB = operations.foldLeft(acc)((rgb, bo) => bo.operate(rgb))
  override def toString: String = name
}

class FilterOperation(name: String, n: Int, filterOp: (List[RGB], RGB => Double) => Double) extends BaseOperation(name) {
  override def operate(rgb: RGB): RGB = rgb

  def filter(neighborRGB: List[RGB]): RGB = {
    new RGB(filterOp(neighborRGB, rgb => rgb.red), filterOp(neighborRGB, rgb => rgb.green), filterOp(neighborRGB, rgb => rgb.blue))
  }

  def operate(neighbors: List[(Int, Int)], image: Image): RGB = filter(getNeighborRGBS(neighbors, image))

  // todo make dp
  def getNeighborRGBS(neighbors: List[(Int, Int)], image: Image): List[RGB] =
    neighbors.map(coordinate => image.getBufferedImage.getRGB(coordinate._1, coordinate._2))

  def getNeighbor(pixel: (Double, Double), maxWidthHeight: (Int, Int), n: Int): List[(Int, Int)] = {
    for (x <-(pixel._1.toInt - n until pixel._1.toInt + n + 1).toList;
         y <- (pixel._2.toInt - n until pixel._2.toInt + n + 1).toList
         if x >= 0 && x < maxWidthHeight._1 && y >= 0 && y < maxWidthHeight._2) yield (x, y)
  }

  override def apply(image: Image, selection: List[Selection]): Image = {
    val img = image.getBufferedImage
    val borders: (Int, Int) = (img.getWidth, img.getHeight)
    for (x <- 0 until img.getWidth;
         y <- 0 until img.getHeight
         if selection.isEmpty || isPixelInSelection(image.actualCoordinates(x, y), selection)) {
      img.setRGB(x, y, operate(getNeighbor((x, y), borders, n), image).limit())
    }
    image.deepCopy()
  }
}

@SerialVersionUID(110L)
object OperationManager extends Serializable {
  val composites: ObservableBuffer[CompositeOperation] = new ObservableBuffer[CompositeOperation]
  val dataOperations = "dataOperations.temp"

  def findComposite(name: String): BaseOperation = composites.find(c => c.name == name).get
  def removeComposite(name: String): Unit = composites.remove(composites.indexOf(findComposite(name)))

  def write(): Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream(dataOperations))
    oos.writeObject(composites.toList)
    oos.close()
  }

  def read(): Unit = {
    val ois = new ObjectInputStream(new FileInputStream(dataOperations))
    val list: List[CompositeOperation] = ois.readObject().asInstanceOf[List[CompositeOperation]]
    ois.close()
    composites.clear()
    composites.addAll(list)
  }
  override def toString: String = composites.foldLeft("")((s, co) => s + co.toString)
}

object Operations {
  implicit def simple(f: RGB=>RGB)(name: String): BaseOperation = new SimpleOperation(name, f)

  def add(value: Double): BaseOperation = new SimpleOperation("add", (i: RGB) => i + value)
  def sub(value: Double): BaseOperation = new SimpleOperation("sub", (i: RGB) => i - value)
  def invSub(value: Double): BaseOperation = new SimpleOperation("inv sub", (i: RGB) => i -@ value)
  def mul(value: Double): BaseOperation = new SimpleOperation("mul", (i: RGB) => i * value)
  def div(value: Double): BaseOperation = new SimpleOperation("div", (i: RGB) => i / value)
  def invDiv(value: Double): BaseOperation = new SimpleOperation("inv div", (i: RGB) => i /@ value)

  def pow(value: Double): BaseOperation = new SimpleOperation("pow", (i: RGB) => i pow value)
  def log(value: Double): BaseOperation = new SimpleOperation("log", (i: RGB) => i log value)
  def min(value: Double): BaseOperation = new SimpleOperation("min", (i: RGB) => i min value)
  def max(value: Double): BaseOperation = new SimpleOperation("max", (i: RGB) => i max value)

  def abs(value: Double = 0): BaseOperation = new SimpleOperation("abs", (i: RGB) => i.abs)
  def greyscale(value: Double = 0): BaseOperation =  new SimpleOperation("greyscale", (i: RGB) => i.toGrey)
  def invert(value: Double = 0): BaseOperation = Operations.invSub(1)

  def crop(): BaseOperation = new CropOperation()
  def fill(): BaseOperation = new FillOperation()

  def median(n: Int): BaseOperation = new FilterOperation("median", n, medianFilerOp)
  def ponder(n: Int): BaseOperation = new FilterOperation("ponder", n, ponderFilerOp)

  // todo create a map
  def call(name: String)(argument: Double): BaseOperation = name match {
    case "add" => add(argument)
    case "sub" => sub(argument)
    case "inv sub" => invSub(argument)
    case "mul" => mul(argument)
    case "div" => div(argument)
    case "inv div" => invDiv(argument)

    case "pow" => pow(argument)
    case "log" => log(argument)
    case "abs" => abs(argument)
    case "min" => min(argument)
    case "max" => max(argument)

    case "greyscale" => greyscale(argument)
    case "invert" => invert(argument)

    case _ => OperationManager.findComposite(name)
  }

  def needsArgument(op: Double => BaseOperation): Boolean = needsArgument(op(0).name)

  def needsArgument(name: String): Boolean = name match {
    case "limit" | "abs" | "greyscale" | "invert" | "fill" => true
    case _ => false
  }

  def medianFilerOp(neighborRGB: List[RGB], colorPick: RGB => Double): Double = {
    val neighborSize: Int = neighborRGB.size
    val sorted: List[Double] = neighborRGB.map(rgb => colorPick(rgb)).sortWith(_<_)
    val middle: Int = neighborSize / 2
    val left: Double = sorted(middle)
    if (neighborSize % 2 == 0) {
      val right: Double = sorted(middle + 1)
      (left + right) / 2
    } else {
      left
    }
  }

  def ponderFilerOp(neighborRGB: List[RGB], colorPick: RGB => Double): Double = {
    neighborRGB.foldLeft(0.0)((acc, color) => acc + colorPick(color)) / neighborRGB.size
  }
}

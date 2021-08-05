package backend.layers

import scala.language.implicitConversions

case class RGB(red: Double, green: Double, blue: Double) {
  private def limit(d: Double): Double = if (d < 0) 0 else if (d > 1) 1 else d
  private def rgbOp(const: Double)(f: (Double, Double) => Double): RGB = new RGB(f(red, const), f(blue, const), f(green, const))

  def +(const: Double): RGB = rgbOp(const)((x, y) => limit(x + y))
  def -(const: Double): RGB = rgbOp(const)((x, y) => limit(x - y))
  def -@(const: Double): RGB = rgbOp(const)((x, y) => limit(y - x))
  def *(const: Double): RGB = rgbOp(const)((x, y) => limit(x * y))
  def /(const: Double): RGB = rgbOp(const)((x, y) => limit(x / y))
  def /@(const: Double): RGB = rgbOp(const)((x, y) => limit(y / x))
  def toGrey: RGB = rgbOp((red + green + blue) / 3.0)((_, y) => limit(y))
}

object RGB {
  private def colorToInt(c: Double)(shift: Int => Int): Int = shift((c * 255.0).toInt)
  private def intToColor(int: Int)(shift: Int => Int): Double = (shift(int) & 0xFF) / 255.0
  implicit def toInt(rgb: RGB): Int = colorToInt(rgb.red)(i => i << 16) | colorToInt(rgb.green)(i => i << 8) | colorToInt(rgb.blue)(i => i << 0)
  implicit def toRGB(int: Int): RGB = RGB(intToColor(int)(i => i >> 16), intToColor(int)(i => i >> 8), intToColor(int)(i => i >> 0))
}

object M {
  def main(args: Array[String]): Unit = {
    val r = new RGB(0.5, 1, 1)
    println(r -@ 0.75)
  }
}

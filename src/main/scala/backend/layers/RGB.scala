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
}

object RGB {
  private def colorToInt(c: Double): Int = (c * 255.0).toInt
  implicit def toInt(rgb: RGB): Int = colorToInt(rgb.red) << 16 | colorToInt(rgb.green) << 8 | colorToInt(rgb.blue) << 0
  implicit def toRGB(int: Int): RGB = RGB((int >> 16) & 0xFF, (int >> 8) & 0xFF, (int >> 0) & 0xFF)
}

object M {
  def main(args: Array[String]): Unit = {
    val r = new RGB(0.5, 1, 1)
    println(r -@ 0.75)
  }
}

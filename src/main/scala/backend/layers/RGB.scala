package backend.layers

import backend.layers.RGB.toInt

import scala.language.implicitConversions

// 0->1 == 0->255
case class RGB(red: Double, green: Double, blue: Double) {
  def this(r: Int, g: Int, b: Int) = this(r / 255.0, g / 255.0, b / 255.0)
  private def limit(d: Double): Double = if (d < 0) 0 else if (d > 1) 1 else d
  private def operation(const: Double)(f: (Double, Double) => Double): RGB =
    new RGB(f(red, const), f(green, const), f(blue, const))

  def +(const: Double): RGB = operation(const)((x, y) => x + y)
  def -(const: Double): RGB = operation(const)((x, y) => x - y)
  def -@(const: Double): RGB = operation(const)((x, y) => y - x)
  def *(const: Double): RGB = operation(const)((x, y) => x * y)
  def /(const: Double): RGB = operation(const)((x, y) => x / y)
  def /@(const: Double): RGB = operation(const)((x, y) => y / x)

  def pow(const: Double): RGB = operation(const)((x, y) => math.pow(x, y))
  def log(const: Double): RGB = operation(const)((x, y) => math.log(x) /  math.log(y))
  def abs: RGB = operation(0)((x, _) => math.abs(x))
  def min(const: Double): RGB = operation(const)((x,y) => math.min(x, y))
  def max(const: Double): RGB = operation(const)((x,y) => math.max(x, y))

  def toGrey: RGB = operation((red + green + blue) / 3.0)((_, y) => y)
  def limit: RGB = new RGB(limit(red), limit(green), limit(blue))

  override def toString: String = s"RGB($red, $green, $blue)"
}

object RGB {
  implicit def toInt(rgb: RGB): Int = (255 << 24) + ((rgb.red * 255.0).round.toInt << 16) + ((rgb.green * 255.0).round.toInt << 8) + ((rgb.blue * 255.0).round.toInt << 0)
  implicit def toRGB(color: Int): RGB = new RGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff) >> 0)
}

object M {
  def main(args: Array[String]): Unit = {
    val dr = 92/255.0
    val dg = 156/255.0
    val db = 46/255.0
    val r = new RGB(dr, dg, db)
    println(s"r $r, ${toInt(r)}")
    val rr = new RGB(r.red + 0.05, r.green + 0.05, r.blue + 0.05)
    println(s"rr $rr, ${toInt(rr)}")
    val rrr = new RGB(rr.red + 0.05, rr.green + 0.05, rr.blue + 0.05)
    println(s"rrr $rrr, ${toInt(rrr)}")
    val value: Int = r
    val rgb: RGB = -10707922
    println(rgb)
  }
}

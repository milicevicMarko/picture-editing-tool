package backend.io

import backend.layers.{Image, ImageManager}

import java.awt.image.BufferedImage
import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

object ResourceManager {
  var out = true
  def test(): Unit = {
    if (out)
      ImageManager.writeImages()
    else
      ImageManager.readImages()
    out = !out
  }
}

package backend.io

import backend.engine.SelectionManager
import backend.layers.{Image, ImageManager}

import java.awt.image.BufferedImage
import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

object ResourceManager {
  var out = true
  def test(): Unit = {
    if (out)
//      ImageManager.writeImages()
      SelectionManager.test()
    else
//      ImageManager.readImages()
      SelectionManager.read()
    out = !out
  }
}

package backend.io

import backend.layers.{Image, ImageManager}

import java.awt.image.BufferedImage
import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

object ResourceManager {
  def save(): Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream("test.txt"))
    oos.writeObject(ImageManager.imageBuffer.head.writeObject(oos))
    oos.close()
  }

  def load(): Unit = {
//    val ois = new ObjectInputStream(new FileInputStream("test.txt"))
//    val stock = ImageManager.imageBuffer.head.readObject(ois)
//    println(stock)
//    ois.close()
  }

  var out = true
  def test(): Unit = {
    if (out) save() else {
      val file: File = new File("test.txt")
      val img: Image = Image.readObject(file)
      ImageManager.add(img)
    }
    out = !out
  }
}

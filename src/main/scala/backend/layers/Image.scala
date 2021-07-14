package backend.layers

import backend.io.{FileBrowser, FileImport}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Image {
  // what happens if file import is aborted
  val file: File = FileImport.importFile()
  val name: String = file.getName
  lazy val bufferedImage: BufferedImage = FileImport.loadImage(file)
  // add CardView
  // add Image
}

sealed abstract class SaveType
case class SaveImage() extends SaveType
case class SaveAsImage() extends SaveType

object FileExport2 {
  def tryToSave(saveType: SaveType)(image: Image): Unit = saveType match {
    case SaveImage() => saveFile()(image)
    case SaveAsImage() => saveFile()(image)
  }

  private def saveFile()(image: Image): Boolean = {
    ImageIO.write(image.bufferedImage, image.name, image.file)
  }
}
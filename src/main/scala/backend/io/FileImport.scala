package backend.io

import backend.layers.ImageManager

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileImport {
  def importFile(): Unit = FileBrowser.chooseImportMultiplePath() match {
    case paths: List[String] => paths.foreach(path => FileBrowser.getType(path) match {
      case "cool" => ResourceManager().read(path)
      case "png" | "jpg" | "jpeg" => ImageManager add path
      case _ => println("error importing")
    })
    case _ => println("Canceled")
  }

  def loadImage(path: String): BufferedImage = loadImage(new File(path))
  def loadImage(file: File): BufferedImage = ImageIO.read(file)
}

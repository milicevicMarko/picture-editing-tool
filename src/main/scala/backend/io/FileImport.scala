package backend.io

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileImport {
  def importFile(): File = FileBrowser.chooseFileImport()

  def loadImage(path: String): BufferedImage = loadImage(new File(path))
  def loadImage(file: File): BufferedImage = ImageIO.read(file)
}

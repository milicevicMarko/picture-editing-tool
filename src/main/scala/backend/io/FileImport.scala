package backend.io

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileImport {
  def loadPicture(path: String): BufferedImage = ImageIO.read(new File(path))

  def loadPicture(file: File): BufferedImage = ImageIO.read(file)
}

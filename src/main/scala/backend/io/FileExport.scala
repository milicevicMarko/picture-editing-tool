package backend.io

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileExport {
  def saveFile(picture: BufferedImage, format: String): Boolean =
    saveFile(picture, format, FileBrowser.getCurrentDirectory)

  def saveFile(picture: BufferedImage, format: String, fileExported: File): Boolean =
    ImageIO.write(picture, format, fileExported)
}

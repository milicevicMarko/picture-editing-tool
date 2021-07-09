package backend.io

import scalafx.stage.Stage

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileImport {
  def importFile(stage: Stage): File = FileBrowser.openFileChooser(isImport = true, stage)

  def loadImage(file: File): BufferedImage = ImageIO.read(file)
}

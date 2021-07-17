package backend.io

import backend.engine.Engine
import backend.layers.Image
import scalafx.stage.Stage

import java.io.File
import javax.imageio.ImageIO

abstract class SaveCommand
case class Save() extends SaveCommand
case class SaveAs() extends SaveCommand

object FileExport {

  def tryToSave(saveCommand: SaveCommand)(image: Image): Boolean = saveCommand match {
    case Save() => ImageIO.write(image.getImage, "jpg", new File(image.getPath))
    case SaveAs() => ImageIO.write(image.getImage, "jpg", new File(FileBrowser.chooseExportPath()))
  }

  def tryToSave1(stageOption: Option[Stage]): Unit = Engine.getImageOption match {
    case Some(_) => saveFile(stageOption)
    case None => println("Nothing to save")
  }

  def saveFile(stageOption: Option[Stage]): Boolean = stageOption match {
    case Some(_) => saveFile(FileBrowser.chooseFileExport())
    case None => ??? // saveFile(FileBrowser.getCurrentFile)
  }

  private def saveFile(fileExported: File): Boolean = {
    if (fileExported != null && Engine.getImageFile.getName.nonEmpty)
      ImageIO.write(Engine.getImage, Engine.getImageFile.getName.split('.').toList.tail.head, fileExported)
    else
      println("Save canceled"); false
  }
  private def saveFile(path: String): Boolean = {
    if (path.nonEmpty && Engine.getImageFile.getName.nonEmpty)
      ImageIO.write(Engine.getImage, Engine.getImageFile.getName.split('.').toList.tail.head, new File(path))
    else
      println("Save canceled"); false
  }



}

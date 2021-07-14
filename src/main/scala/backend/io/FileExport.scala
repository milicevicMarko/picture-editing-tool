package backend.io

import backend.engine.Engine
import scalafx.stage.Stage


import java.io.File
import javax.imageio.ImageIO

object FileExport {

  def tryToSave(stageOption: Option[Stage]): Unit = Engine.getImageOption match {
    case Some(_) => saveFile(stageOption)
    case None => println("Nothing to save")
  }

  def saveFile(stageOption: Option[Stage]): Boolean = stageOption match {
    case Some(stage) => saveFile(FileBrowser.chooseFileExport())
    case None => saveFile(FileBrowser.getCurrentFile)
  }

  private def saveFile(fileExported: File): Boolean = {
    if (fileExported != null && Engine.getImageFile.getName.nonEmpty)
      ImageIO.write(Engine.getImage, Engine.getImageFile.getName.split('.').toList.tail.head, fileExported)
    else
      println("Save canceled"); false
  }

}

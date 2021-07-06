package backend.io

import scalafx.stage.{FileChooser, Stage, Window}

import java.io.File

object FileBrowser {
  val extensions: List[String] = List("*", ".txt")
  val importTitle = "Import File"
  private def getDesktopPath = System.getProperty("user.home") + "/Desktop"

  def openFileThroughBrowser(ti: String, stage: Stage): File = new FileChooser {
    title = ti
    initialDirectory = new File(getDesktopPath)
  }.showOpenDialog(stage)

  def importFile(stage: Option[Stage]): File = stage match {
    case Some(s) => openFileThroughBrowser(importTitle, s)
    case None => throw new ExceptionInInitializerError()
  }
}

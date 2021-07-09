package backend.io

import scalafx.stage.{FileChooser, Stage}

import java.io.File

trait Constants {
  val Extensions: List[String] = List("*", ".txt", ".JPG", ".JPEG", ".PNG")
  val ImportTitle = "Import File"
  def DesktopPath: String = System.getProperty("user.home") + "/Desktop"
  val InitialFile: File = new File(DesktopPath)
}

// todo - this file should have only file chooser code!
object FileBrowser extends Constants {
  var currentDirectory: File = InitialFile

  private def openFileThroughBrowser(ti: String, stage: Stage): File = new FileChooser {
    title = ti
    initialDirectory = InitialFile
  }.showOpenDialog(stage)

  def importFile(stage: Option[Stage]): File = {
    val imported = openFileThroughBrowser(ImportTitle, stage.getOrElse(throw new ExceptionInInitializerError))
    currentDirectory = imported
    imported
  }

  def getCurrentDirectory: File = currentDirectory
}

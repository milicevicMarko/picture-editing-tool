package backend.io

import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Stage}

import java.io.File

trait Constants {
  val Extensions: Seq[String] = Seq("*.jpg", "*.jpeg", "*.png")
  val PictureExtensionFilter: ExtensionFilter = new ExtensionFilter("Picture", Extensions)
  val ImportTitle = "Open File"
  val ExportTitle = "Save File"
  def DesktopPath: String = System.getProperty("user.home") + "/Desktop"
  def getDirectoryFromFile(file: File): File = new File(file.getPath + "/..")
  val InitialDirectory: File = new File(DesktopPath)
}

object FileBrowser extends Constants {
  var currentFile: File = InitialDirectory
  def getCurrentDirectory: File = getDirectoryFromFile(currentFile)
  def getCurrentFile: File = currentFile

  def openFileChooser(isImport: Boolean = true, stage: Stage): File = {
    val fileChooser = new FileChooser {
      title = if (isImport) ImportTitle else ExportTitle
      initialDirectory = InitialDirectory
    }
    fileChooser.extensionFilters.addAll(PictureExtensionFilter)
    val file = if (isImport) fileChooser.showOpenDialog(stage) else fileChooser.showSaveDialog(stage)
    if (file != null) currentFile = file
    file
  }

}

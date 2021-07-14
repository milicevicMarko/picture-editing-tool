package backend.io

import frontend.main.MainControllerApp
import scalafx.application.JFXApp3
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.FileChooser

import java.io.File

trait Constants {
  val Extensions: Seq[String] = Seq("*.jpg", "*.jpeg", "*.png")
  val PictureExtensionFilter: ExtensionFilter = new ExtensionFilter("Picture", Extensions)
  val Title = "File Browser"
  def DesktopPath: String = System.getProperty("user.home")
  def getDirectoryFromFile(file: File): File = new File(file.getPath + "/..")
  val InitialDirectory: File = new File(DesktopPath)
}

object FileBrowser extends Constants {
  var currentFile: File = InitialDirectory
  val primaryStage: JFXApp3.PrimaryStage = MainControllerApp.stage
  def getCurrentDirectory: File = getDirectoryFromFile(currentFile)
  def getCurrentFile: File = currentFile

  val fileChooser: FileChooser = {
    val fc = new FileChooser {
      title = Title
      initialDirectory = InitialDirectory
    }
    fc.extensionFilters.addAll(PictureExtensionFilter)
    fc
  }

  def updateCurrentFile(file: File): File = {
    currentFile = file
    file
  }

  def chooseFileImport(): File = updateCurrentFile(fileChooser.showOpenDialog(primaryStage))
  def chooseFileExport(): File = updateCurrentFile(fileChooser.showSaveDialog(primaryStage))
}

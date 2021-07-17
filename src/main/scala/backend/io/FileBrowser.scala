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
  val DefaultPath: String = System.getProperty("user.home")

  def getDirectoryFromFile(file: File): File = new File(file.getPath + "/..")
}

object FileBrowser extends Constants {
  val primaryStage: JFXApp3.PrimaryStage = MainControllerApp.stage

  val fileChooser: FileChooser = new FileChooser {
      title = Title
      initialDirectory = new File(DefaultPath)
      extensionFilters.addAll(PictureExtensionFilter)
  }

  def chooseImportPath(): String = fileChooser.showOpenDialog(primaryStage) match {
    case f:File => f.getPath
    case null => ""
  }

  def chooseExportPath(): String = fileChooser.showSaveDialog(primaryStage) match {
    case f:File => f.getPath
    case null => ""
  }

  def chooseFileImport(): File = fileChooser.showOpenDialog(primaryStage)
  def chooseFileExport(): File = fileChooser.showSaveDialog(primaryStage)
}

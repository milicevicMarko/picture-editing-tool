package backend.io

import frontend.main.MainControllerApp
import scalafx.application.JFXApp3
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.FileChooser

import java.io.File

trait Constants {
  val PictureExtensions: Seq[String] = Seq("*.jpg", "*.jpeg", "*.png")
  val MyExtensions: Seq[String] = Seq("*.cool")
  val PictureExtensionFilter: ExtensionFilter = new ExtensionFilter("Picture", PictureExtensions)
  val MyExtensionFilter: ExtensionFilter = new ExtensionFilter("FPhotoshop", MyExtensions)
  val Title = "File Browser"
  val DefaultPath: String = System.getProperty("user.home")
  val DebugPath: String = "src/test/test_images"

  def getDirectoryFromFile(file: File): File = new File(file.getPath + "/..")
}

object FileBrowser extends Constants {
  val primaryStage: JFXApp3.PrimaryStage = MainControllerApp.stage

  val fileChooser: FileChooser = new FileChooser {
      title = Title
      initialDirectory = new File(DebugPath)
      extensionFilters.addAll(PictureExtensionFilter, MyExtensionFilter)
  }

  def chooseImportPath(): String = fileChooser.showOpenDialog(primaryStage) match {
    case f:File => f.getPath
    case null => ""
  }

  def chooseImportMultiplePath(): List[String] = fileChooser.showOpenMultipleDialog(primaryStage) match {
    case lf: List[File] => lf.map(f => f.getPath)
    case _ => Nil
  }

  def chooseExportPath(): String = fileChooser.showSaveDialog(primaryStage) match {
    case f:File => f.getPath
    case null => ""
  }

  def getType(path: String): String = new File(path).getName.split('.')(1)

  def chooseFileImport(): File = fileChooser.showOpenDialog(primaryStage)
  def chooseFileExport(): File = fileChooser.showSaveDialog(primaryStage)
}

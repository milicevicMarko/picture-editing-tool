package backend.io

import backend.layers.ImageManager

abstract class SaveCommand(path: String = "")
case class Save() extends SaveCommand() {
  ResourceManager().write()
}
case class SaveAsPNG(p: String) extends SaveCommand(p) {
  ImageManager.write(p, "PNG")
}
case class SaveAsJPG(p: String) extends SaveCommand(p) {
  ImageManager.write(p, "JPG")
}
case class SaveAsCool(p: String) extends SaveCommand(p) {
  ResourceManager().write(p)
}

object FileExport {
  def tryToSave(path: String = ""): Unit =
    if (path.nonEmpty)
      FileBrowser.getType(path) match {
        case "png" => SaveAsPNG(path)
        case "jpg" | "jpeg" => SaveAsJPG(path)
        case "cool" => SaveAsCool(path)
      }
    else
      Save()

  def save(): Unit= tryToSave()

  // if image is originally png, it cannot be saved as jpg
  // vice versa works
  def saveAs(): Unit = {
    val path = FileBrowser.chooseExportPath()
    if (path.nonEmpty) tryToSave(path)
  }
}

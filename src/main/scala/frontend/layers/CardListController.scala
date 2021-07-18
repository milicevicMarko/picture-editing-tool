package frontend.layers

import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml

trait CardListControllerInterface {
  def setData(imageView: ImageView, name: String, number: Int)
}

@sfxml
class CardListController(layerNumber: Label, thumbnail: ImageView, fileName: Label) extends CardListControllerInterface {
  override def setData(imageView: ImageView, name: String, number: Int): Unit = {
    thumbnail.setImage(imageView.getImage)
    fileName.text = name
    layerNumber.text = number.toString
  }
}

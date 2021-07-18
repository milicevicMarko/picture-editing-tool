package frontend.layers

import backend.layers.Image
import javafx.scene.{control => jfxsc}
import scalafx.scene.image.ImageView

class CardView2 (image: Image) {
  val thumbnail: ImageView = image.thumbnail
  val name: String = image.getName
}

class CardViewCell extends jfxsc.ListCell[CardView2] {
  override def updateItem(cv: CardView2, empty: Boolean): Unit = {
    if (empty || cv == null) {
      setText(null)
      setGraphic(null)
    } else {
      setGraphic(cv.thumbnail)
      setText(cv.name)
    }
  }
}

package frontend.layers

import backend.layers.Image
import javafx.scene.{control => jfxsc}

class CardView2 (image: Image) {
  def getImage: Image = image
}

class CardViewCell extends jfxsc.ListCell[CardView2] {
  override def updateItem(cv: CardView2, empty: Boolean): Unit = {
    if (empty || cv == null) {
      setText(null)
      setGraphic(null)
    } else {
      setGraphic(cv.getImage.thumbnail)
      setText(cv.getImage.getName)
    }
  }
}


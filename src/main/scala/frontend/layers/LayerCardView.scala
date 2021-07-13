package frontend.layers

import backend.layers.LayerCard
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{control => jfxsc}
import scalafx.scene.image.ImageView

case class LayerCardView (card: LayerCard, thumbnail: ImageView) {
  override def toString: String = s"This card: ${card.toString}"
}

object LayerCardView{
  // todo understand why it duplicates layer, and why it suddenly cannot be scrolled
  // todo make create auto for opening
  // todo optimize some more
  def update(items: ListView[LayerCardView]): Unit = {
    items.cellFactory = (lv: ListView[LayerCardView]) => {
      new ListCell(new jfxsc.ListCell[LayerCardView] {
        override def updateItem(cardView: LayerCardView, empty: Boolean): Unit = {
          if (empty || cardView == null) {
            setText(null)
            setGraphic(null)
          } else {
            setGraphic(cardView.thumbnail)
            setText(cardView.toString)
          }
        }
      })
    }
  }
}
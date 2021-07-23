package frontend.layers

import backend.layers.{Image, ImageManager}
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{control => jfxsc}
import javafx.scene.input
import scalafx.collections.ObservableBuffer

class CardListView (listView: ListView[Image]) {
  def init(): Unit = {
    val list: ObservableBuffer[Image] = ImageManager.imageBuffer
    listView.setItems(list)
    listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

    listView.cellFactory = _ => new ListCell(new jfxsc.ListCell[Image]{
      addEventFilter[input.MouseEvent](input.MouseEvent.MOUSE_CLICKED, e => {
        if (!isEmpty) {
          val index = getIndex
          // todo cannot click after swap
          if (getItem.isSelected) {
            listView.getSelectionModel.clearSelection(index)
          } else {
            listView.getSelectionModel.select(index)
          }

          if (!e.isControlDown)
            ImageManager.deselectAll()
          getItem.select()
        } else {
          ImageManager.deselectAll()
          listView.getSelectionModel.clearSelection()
        }
        e.consume()
      })

      override def updateItem(t: Image, b: Boolean): Unit = {
        super.updateItem(t, b)
        if (b || t == null) setGraphic(null)
        else setGraphic(t.cardView.card)
      }
    })

  }

  init()
}

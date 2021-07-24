package frontend.layers

import backend.layers.{Image, ImageManager}
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{input, control => jfxsc}
import scalafx.collections.ObservableBuffer

class CardListView (listView: ListView[Image]) {
  def deselectAll(): Unit = {
    ImageManager.deselectAll()
    listView.getSelectionModel.clearSelection()
  }

  def selectAll(): Unit = {
    ImageManager.selectAll()
    listView.getSelectionModel.selectAll()
  }

  def init(): Unit = {
    val list: ObservableBuffer[Image] = ImageManager.imageBuffer
    listView.setItems(list)
    listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

    listView.cellFactory = _ => new ListCell(new jfxsc.ListCell[Image]{
      addEventFilter[input.MouseEvent](input.MouseEvent.MOUSE_CLICKED, e => {
        if (isEmpty || !e.isControlDown) deselectAll()

        if (!isEmpty) {
          if (getItem.isSelected) {
            listView.getSelectionModel.clearSelection(getIndex)
          } else {
            listView.getSelectionModel.select(getIndex)
          }
          getItem.select()
        }

        e.consume()
      })
      override def updateItem(t: Image, b: Boolean): Unit = {
        super.updateItem(t, b)
        if (b || t == null) setGraphic(null)
        else setGraphic(t.cardView.card)
      }
    })

    listView.addEventFilter[KeyEvent](KeyEvent.KEY_PRESSED, e => {
      if (e.isControlDown && e.getCode == KeyCode.A) selectAll()
      e.consume()
    })
  }

  init()
}

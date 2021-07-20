package frontend.layers

import backend.layers.{Image, ImageManager}
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{control => jfxsc}
import scalafx.collections.ObservableBuffer

import scala.jdk.CollectionConverters._

class CardListView (listView: ListView[Image]) {

  val list: ObservableBuffer[Image] = ImageManager.imageBuffer
  listView.setItems(list)
  listView.cellFactory = _ => {
    new ListCell(new jfxsc.ListCell[Image]{
      override def updateItem(t: Image, b: Boolean): Unit = {
        super.updateItem(t, b)
        if (b || t == null) setGraphic(null)
        else setGraphic(t.cardView.card)
      }
    })
  }

  listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

  listView.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[Image] {
    override def changed(observableValue: ObservableValue[_ <: Image], oldValue: Image, newValue: Image): Unit =
      ImageManager setSelected listView.getSelectionModel.getSelectedItems.asScala.toList
  })
}

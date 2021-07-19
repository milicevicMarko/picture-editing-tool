package frontend.layers

import backend.layers.{Image, ImageManager}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ObservableList
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{control => jfxsc}

import scala.jdk.CollectionConverters._

// todo how to connect to ImageManager for seamless additions
class CardListView (listView: ListView[CardView]) {

  val list: ObservableList[CardView] = ImageManager.observableList
  listView.setItems(list)
  listView.cellFactory = _ => {
    new ListCell(new jfxsc.ListCell[CardView]{
      override def updateItem(t: CardView, b: Boolean): Unit = {
        super.updateItem(t, b)
        if (b || t == null) setGraphic(null)
        else setGraphic(t.card)
      }
    })
  }

  listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

  listView.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[CardView] {
    override def changed(observableValue: ObservableValue[_ <: CardView], oldValue: CardView, newValue: CardView): Unit =
      ImageManager setSelected listView.getSelectionModel.getSelectedItems.asScala.map(cv => cv.image).toList
  })
}

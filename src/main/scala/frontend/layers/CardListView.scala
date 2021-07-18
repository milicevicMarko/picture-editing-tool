package frontend.layers

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import scalafx.scene.control.ListView
import javafx.scene.{control => jfxsc}
import scalafx.scene.layout.AnchorPane

class CardListView (listView: ListView[CardView]) {
  // todo https://stackoverflow.com/questions/33592308/javafx-how-to-put-imageview-inside-listview
  val list: ObservableList[CardView] = FXCollections.observableArrayList()
  listView.setItems(list)
//  listView.cellFactory = lv => new ListCell(new CardListVIewJFX2)
  listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

  listView.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[CardView] {
    override def changed(observableValue: ObservableValue[_ <: CardView], oldValue: CardView, newValue: CardView): Unit = {
      listView.getSelectionModel.getSelectedItems.forEach(n => println(n.name))
    }
  })

  def add(cv: CardView): Unit = list.add(cv)
}

class CardListVIewJFX2 extends jfxsc.ListCell[AnchorPane] {
  override def updateItem(cv: AnchorPane, empty: Boolean): Unit = {
    if (empty || cv == null) {
      //      setText(null)
      //      setGraphic(null)
      setClip(null)
    } else {
      //      setText( cv.name)
      //      setGraphic(cv.thumbnail)
      setClip(cv)
    }
  }
}

class CardListVIewJFX extends jfxsc.ListCell[CardView] {
  override def updateItem(cv: CardView, empty: Boolean): Unit = {
    if (empty || cv == null) {
//      setText(null)
//      setGraphic(null)
      setClip(null)
    } else {
//      setText( cv.name)
//      setGraphic(cv.thumbnail)
      setClip(cv.card)
    }
  }
}
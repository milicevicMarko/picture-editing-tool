package frontend.layers

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{control => jfxsc}

class CardListVIewJFX extends jfxsc.ListCell[CardView] {
    override def updateItem(cv: CardView, empty: Boolean): Unit = {
      if (empty || cv == null) {
        setText(null)
        setGraphic(null)
      } else {
        setText( cv.name)
        setGraphic(cv.thumbnail)
      }
    }
}

class CardListView (listView: ListView[CardView]) extends ListCell[CardView] {
  val list: ObservableList[CardView] = FXCollections.observableArrayList()
  listView.setItems(list)
  listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

  listView.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[CardView] {
    override def changed(observableValue: ObservableValue[_ <: CardView], oldValue: CardView, newValue: CardView): Unit = {
      listView.getSelectionModel.getSelectedItems.forEach(n => println(n.name))
    }
  })

//  override def updateItem(cv: CardView, empty: Boolean): Unit = {
//    if (empty || cv == null) {
//      setText(null)
//      setGraphic(null)
//    } else {
//      setText( cv.name)
//      setGraphic(cv.thumbnail)
//    }
//  }

  def add(cv: CardView): Unit = list.add(cv)

}

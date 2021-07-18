package frontend.layers

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import scalafx.scene.control.{ListCell, ListView}
import javafx.scene.{control => jfxsc}
import scalafx.scene.layout.AnchorPane



class CardListView (listView: ListView[AnchorPane]) extends ListCell[CardView] {
  val list: ObservableList[AnchorPane] = FXCollections.observableArrayList()
  listView.setItems(list)
//  listView.cellFactory = lv => new ListCell(new CardListVIewJFX2)
  listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

  listView.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[AnchorPane] {
    override def changed(observableValue: ObservableValue[_ <: AnchorPane], oldValue: AnchorPane, newValue: AnchorPane): Unit = {
      // listView.getSelectionModel.getSelectedItems.forEach(n => println(n.getChildren.))
      println("click")
    }
  })

  def add(cv: AnchorPane): Unit = list.add(cv)
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
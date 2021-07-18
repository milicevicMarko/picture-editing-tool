package frontend.layers

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import scalafx.scene.control.ListView

class CardListView (listView: ListView[String]) {
  val list: ObservableList[String] = FXCollections.observableArrayList()
  listView.setItems(list)
  listView.getSelectionModel.setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE)

  listView.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[String] {
    override def changed(observableValue: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = listView.getSelectionModel.getSelectedItems.forEach(n => println(n))
  })


  def add(s: String): Unit = list.add(s)

}

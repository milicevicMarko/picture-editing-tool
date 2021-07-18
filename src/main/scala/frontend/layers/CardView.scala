package frontend.layers

import backend.layers.Image
import scalafx.scene.control.ListCell
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL

class CardView(image: Image) extends ListCell[CardView] {
  val thumbnail: ImageView = image.thumbnail
  val name: String = image.getName
  // todo https://stackoverflow.com/questions/47511132/javafx-custom-listview

  val url: URL = getClass.getResource("resources/CardList.fxml")
  val loader = new FXMLLoader(url, new DependenciesByType(Map()))
  val card: javafx.scene.layout.AnchorPane = loader.load()
  val cardListController: CardListControllerInterface = loader.getController()
}
//override def updateItem(cv: CardView, empty: Boolean): Unit = {
//  if (empty || cv == null) {
//  setText(null)
//  setGraphic(null)
//  } else {
//  setGraphic(cv.thumbnail)
//  setText(cv.name)
//  }
//  }

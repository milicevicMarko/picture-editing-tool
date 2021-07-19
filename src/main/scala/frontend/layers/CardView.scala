package frontend.layers

import backend.layers.Image
import scalafx.scene.control.ListCell
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL

class CardView(val image: Image) extends ListCell[CardView] {
  val url: URL = getClass.getResource("resources/CardList.fxml")
  val loader = new FXMLLoader(url, new DependenciesByType(Map()))
  val card: javafx.scene.Node = loader.load()
  val cardListController: CardListControllerInterface = loader.getController()
  cardListController.setData(image.thumbnail, image.name, image.index + 1)
}

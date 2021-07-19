package frontend.layers

import backend.layers.Image
import scalafx.scene.control.ListCell
import scalafx.scene.image.ImageView
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL

class CardView(image: Image) extends ListCell[CardView] {
  val thumbnail: ImageView = image.thumbnail
  val name: String = image.getName

  val url: URL = getClass.getResource("resources/CardList.fxml")
  val loader = new FXMLLoader(url, new DependenciesByType(Map()))
  val card: javafx.scene.Node = loader.load()
  val cardListController: CardListControllerInterface = loader.getController()
  cardListController.setData(thumbnail, name, image.index + 1)

  override def toString(): String = s"Layer: ${image.index + 1} \t ${image.getName}"
}

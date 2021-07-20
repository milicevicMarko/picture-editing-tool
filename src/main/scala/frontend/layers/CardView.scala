package frontend.layers

import backend.layers.Image
import javafx.beans.value
import javafx.beans.value.ObservableValue
import scalafx.scene.control.ListCell
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL

class CardView(val image: Image) extends ListCell[CardView] {
  val url: URL = getClass.getResource("resources/CardList.fxml")
  val loader = new FXMLLoader(url, new DependenciesByType(Map()))
  val card: javafx.scene.Node = loader.load()
  val cardListController: CardListControllerInterface = loader.getController()
  cardListController.setData(image.thumbnail, image.name, image.index + 1)

  cardListController.getOpacitySlider.valueProperty().addListener(new value.ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      val opacity: Double = newValue.doubleValue() / 100
      image.setOpacity(opacity)
    }
  })
}

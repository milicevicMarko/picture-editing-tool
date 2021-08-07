package frontend.layers

import backend.layers.{Image, ImageManager}
import frontend.utils.UIUtils
import javafx.beans.value
import javafx.beans.value.ObservableValue
import scalafx.scene.control.ListCell
import scalafx.scene.image.ImageView
import scalafxml.core.{DependenciesByType, FXMLLoader}

class CardView(val image: Image) extends ListCell[CardView] {
  val thumbnail: ImageView = UIUtils.imageToThumbnail(image.getImage)
  val loader = new FXMLLoader(getClass.getResource("resources/CardList.fxml"), new DependenciesByType(Map()))
  val card: javafx.scene.Node = loader.load()
  val cardListController: CardListControllerInterface = {
    val controller: CardListControllerInterface = loader.getController()
    controller.setData(thumbnail, image.name, image.index + 1)
    controller.getOpacitySlider.valueProperty().addListener(new value.ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        val opacity: Double = newValue.doubleValue() / 100
        image.setOpacity(opacity)
      }
    })
    controller.getUpButton.onAction = _ => ImageManager.moveUp(image)
    controller.getDownButton.onAction = _ => ImageManager.moveDown(image)
    controller.getDeleteButton.onAction = _ => ImageManager.remove(image)
    controller.getDuplicateButton.onAction = _ => ImageManager.duplicate(image)
    controller
  }

  def updateIndex(): Unit = cardListController.getLayerNumber.setText(s"${image.index + 1}")
}

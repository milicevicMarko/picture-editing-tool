package frontend.layers

import backend.layers.{Image, ImageManager}
import javafx.beans.value
import javafx.beans.value.ObservableValue
import scalafx.scene.control.ListCell
import scalafx.scene.image.ImageView
import scalafxml.core.{DependenciesByType, FXMLLoader}

import scala.language.implicitConversions

class CardView(val image: Image) extends ListCell[CardView] {
  val thumbnail: ImageView = Image.imageToThumbnail(image.getBufferedImage)
  val loader = new FXMLLoader(getClass.getResource("resources/CardList.fxml"), new DependenciesByType(Map()))
  val card: javafx.scene.Node = loader.load()
  val cardListController: CardListControllerInterface = {
    val controller: CardListControllerInterface = loader.getController()

    def activateImage(activate: Boolean): Unit = {
      implicit def boolean2Double(b: Boolean): Double = if (b) 1 else 0
      image.setOpacity(activate)
      image.activate(activate)
      controller.getOpacitySlider.value = activate * 100
      controller.getOpacitySlider.disable = !activate
      controller.toggleVisible(activate)
    }

    controller.setData(thumbnail, image.name, image.index + 1)
    controller.getOpacitySlider.valueProperty().addListener(new value.ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit =
       image.setOpacity(newValue.doubleValue() / 100)
    })
    controller.getUpButton.onAction = _ => ImageManager.moveUp(image)
    controller.getDownButton.onAction = _ => ImageManager.moveDown(image)
    controller.getDeleteButton.onAction = _ => ImageManager.remove(image)
    controller.getDuplicateButton.onAction = _ => ImageManager.duplicate(image)
    controller.getVisibleButton.onAction = _ => activateImage(false)
    controller.getInvisibleButton.onAction = _ => activateImage(true)
    controller
  }

  def updateIndex(): Unit = cardListController.getLayerNumber.setText(s"${image.index + 1}")
}

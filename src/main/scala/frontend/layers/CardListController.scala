package frontend.layers

import javafx.beans.value
import javafx.beans.value.ObservableValue
import scalafx.scene.control.{Button, Label, Slider}
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml

trait CardListControllerInterface {
  def setData(imageView: ImageView, name: String, number: Int)
  def getOpacitySlider: Slider
  def getLayerNumber: Label
  def getDuplicateButton: Button
  def getDeleteButton: Button
  def getUpButton: Button
  def getDownButton: Button
  def getVisibleButton: Button
  def getInvisibleButton: Button
  def toggleVisible(visible: Boolean): Unit
}

@sfxml
class CardListController(layerNumber: Label, thumbnail: ImageView, fileName: Label, opacitySlider: Slider,
                         opacityLabel: Label, upButton: Button, downButton: Button, duplicateButton: Button,
                         deleteButton: Button, visibleButton: Button, invisibleButton: Button) extends CardListControllerInterface {

  opacitySlider.valueProperty().addListener(new value.ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      opacityLabel.textProperty().setValue(String.valueOf(newValue.intValue()) + "%")
    }
  })

  override def setData(imageView: ImageView, name: String, number: Int): Unit = {
    thumbnail.setImage(imageView.getImage)
    fileName.text = name
    layerNumber.text = number.toString
  }

  override def getOpacitySlider: Slider = opacitySlider

  override def getLayerNumber: Label = layerNumber

  override def getUpButton: Button = upButton

  override def getDownButton: Button = downButton

  override def getDuplicateButton: Button = duplicateButton

  override def getDeleteButton: Button = deleteButton

  override def getInvisibleButton: Button = invisibleButton

  override def getVisibleButton: Button = visibleButton

  private def toggleButton(enable: Boolean, button: Button): Unit = {
    button.disable = !enable
    button.visible = enable
  }
  override def toggleVisible(visible: Boolean): Unit = {
    toggleButton(visible, visibleButton)
    toggleButton(!visible, invisibleButton)
  }
}

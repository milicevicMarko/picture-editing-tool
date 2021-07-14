package backend.layers

import backend.engine.Engine.image
import frontend.layers.LayerCardView
import frontend.utils.UIUtils
import scalafx.scene.control.ListView
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane

import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

class LayerManager(listView: ListView[LayerCardView]) {
  val layerList: ListBuffer[LayerCard] = new ListBuffer[LayerCard]

  def addCard(card: LayerCard): Unit = {
    layerList += card
    showCards()
  }

  def addCard(cardName: String, img: BufferedImage): Unit = addCard(new LayerCard(cardName + layerList.size, img))

  def showCards(): Unit = {
    listView.getItems.clear()
    layerList.foreach(c=> listView.getItems.add(c.view))
    LayerCardView.update(listView)
  }

  def updateImage(imageView: ImageView, pane: StackPane): Unit = {
    updateSize(imageView, pane)
    UIUtils.convertImageToImageView(image.get, imageView)
  }

  def updateSize(imageView: ImageView, pane: StackPane): Unit = {
    val img = image.getOrElse(throw new IllegalArgumentException)
    imageView.setPreserveRatio(true)
    if (img.getWidth() >= img.getHeight()) imageView.setFitWidth(pane.getWidth - 100)
    if (img.getWidth() <= img.getHeight()) imageView.setFitHeight(pane.getHeight - 100)
  }
}

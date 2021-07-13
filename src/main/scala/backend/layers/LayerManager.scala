package backend.layers

import frontend.layers.LayerCardView
import scalafx.scene.control.ListView

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
  }
}

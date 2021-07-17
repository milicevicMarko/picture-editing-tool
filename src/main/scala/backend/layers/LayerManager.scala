package backend.layers

import backend.engine.Engine.image
import backend.io.FileImport
import frontend.layers.LayerCardView
import frontend.utils.UIUtils
import scalafx.scene.control.ListView
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane

import scala.collection.mutable.ListBuffer
import java.io.File

class LayerManager(listView: ListView[LayerCardView]) {
  val layerList: ListBuffer[LayerCard] = new ListBuffer[LayerCard]

  def addCard(card: LayerCard): Unit = {
    layerList += card
    showCards()
  }

  def addCard(cardName: String, file: File): Unit = addCard(new LayerCard(cardName + layerList.size, file))

  def showCards(): Unit = {
    listView.getItems.clear()
    layerList.foreach(c=> listView.getItems.add(c.view))
    LayerCardView.update(listView)
  }
}

object LayerManager {
  val layerBuffer: ListBuffer[Image] = new ListBuffer[Image]
//  def addNewLayer(): Unit = layerBuffer.addOne(new Image())
}

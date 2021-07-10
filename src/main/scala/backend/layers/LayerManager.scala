package backend.layers

import frontend.layers.LayerCardView
import scalafx.scene.control.ListView

import scala.annotation.tailrec

class LayerManager(layerListView: ListView[LayerCardView], layerList: LayerList) {
  def this(layerListView: ListView[LayerCardView]) = this(layerListView, new LayerList(Nil))

  def addCard(card: LayerCard): LayerManager = {
    val newManager = new LayerManager(layerListView, layerList add card)
    layerListView.getItems.add(card.view)
    newManager
  }

  def showCards(): Unit = {
    @tailrec
    def showCards(cards: List[LayerCard]): Unit = cards match {
      case c::cs => layerListView.getItems.add(c.view); showCards(cs)
      case Nil => println("Finished iterating trough cards")
    }
    layerListView.getItems.clear()
    showCards(layerList.toList)
  }
}
// todo maybe create a factory that return always a new instance, copying existing
//object LayerManager {
//  var instance: Option[LayerManager] = None
//  def getInstance(listView: ListView[LayerCardView]): LayerManager = instance match {
//    case None => instance = Some(new LayerManager(listView)); instance.get
//    case Some(_) => instance.get
//  }
//}

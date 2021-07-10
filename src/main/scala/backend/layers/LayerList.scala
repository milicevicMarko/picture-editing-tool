package backend.layers

import scala.annotation.tailrec

class LayerList (layers: List[LayerCard]) {
  def add(card: LayerCard): LayerList = {
    @tailrec
    def add(acc: List[LayerCard], cards: List[LayerCard], card: LayerCard): List[LayerCard] = cards match {
      case c::cs => add(c::acc, cs, card)
      case Nil => card::acc
    }

    new LayerList(add(Nil, layers, card))
  }

  def toList: List[LayerCard] = layers
}

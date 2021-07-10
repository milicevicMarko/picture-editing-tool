package frontend.layers

import backend.layers.LayerCard

class LayerCardView (card: LayerCard) {
  override def toString: String = s"This card: ${card.toString}"
}
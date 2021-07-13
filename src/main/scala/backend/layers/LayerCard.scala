package backend.layers

import frontend.layers.LayerCardView

class LayerCard (name: String) {
  val view: LayerCardView = new LayerCardView(this)

  override def toString: String = name
}

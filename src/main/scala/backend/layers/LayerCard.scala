package backend.layers

import frontend.layers.LayerCardView

class LayerCard (name: String) {
  val view: LayerCardView = new LayerCardView(this)

  override def toString: String = s"Hey there, my name is $name"
}

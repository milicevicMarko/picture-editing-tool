package backend.layers

import frontend.layers.LayerCardView
import frontend.utils.UIUtils

import java.awt.image.BufferedImage

class LayerCard (name: String, img: BufferedImage) {
  val view: LayerCardView = new LayerCardView(this, UIUtils.createThumbnailImageViewFromImage(img))
  override def toString: String = name
}

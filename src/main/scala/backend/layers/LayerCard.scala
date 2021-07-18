package backend.layers

import backend.io.FileImport
import frontend.layers.LayerCardView
import frontend.utils.UIUtils

import java.awt.image.BufferedImage
import java.io.File

case class LayerCard (name: String, img: BufferedImage, imgFile: File) {
  def this(name: String, f: File) = this(name, FileImport.loadImage(f), f)

  val view: LayerCardView = new LayerCardView(this, UIUtils.imageToThumbnail(img))
  def getImg: BufferedImage = img
  override def toString: String = name
}
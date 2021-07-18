package frontend.layers

import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml

trait CardListControllerInterface

@sfxml
class CardListController(layerNumber: Label, thumbnail: ImageView, fileName: Label) extends CardListControllerInterface {
}

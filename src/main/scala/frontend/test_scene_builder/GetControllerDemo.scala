package frontend.test_scene_builder

import javafx.scene.Parent
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLLoader}

import java.net.URL

/**
 * Public interface of our controller which will be available through FXMLLoader
 */
trait UnitConverterInterface {
  def setLabelText(s: String): Unit
  def increment(): Unit
}

/** Our controller class, implements UnitConverterInterface */
@sfxml
class DemoController(msg: Label, incrementer: Button)
  extends UnitConverterInterface {
  override def setLabelText(s: String): Unit = msg.text = s
  override def increment(): Unit = {
    val value = msg.getText.toInt + 1
    msg.text = value.toString
  }
}


object GetControllerDemo extends JFXApp {

  // Instead of FXMLView, we create a new ScalaFXML loader
  val url: URL = getClass.getResource("resources/demo.fxml")
  val loader = new FXMLLoader(url, new DependenciesByType(Map()))

  // Load the FXML, the controller will be instantiated
  loader.load()

  // Get the scene root
  val root: Parent = loader.getRoot[jfxs.Parent]

  // Get the controller. We cannot use the controller class itself here,
  // because it is transformed by the macro - but we can use the trait it
  // implements!
  val controller: UnitConverterInterface = loader.getController[UnitConverterInterface]
  controller.setLabelText("0")

  stage = new JFXApp.PrimaryStage() {
    title = "Unit converter"
    scene = new Scene(root)
  }
}
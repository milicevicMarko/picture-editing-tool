package frontend.exit

import javafx.scene.control
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.{Stage, WindowEvent}

trait ExitController{
  val SaveText: String = "Save"
  val SaveAsText: String = "Save As..."
  val CancelText: String = "Cancel"
  val OkText: String = "OK"

  val AlertTitle: String = "Close Confirmation"
  val AlertHeader: String = "Are you sure you want to exit without saving?"
  val AlertContent: String = "All the work done since your last save will be lost."

  val SaveButtonType = new ButtonType(SaveText)
  val SaveAsButtonType = new ButtonType(SaveAsText)

  val ButtonTypes = Seq(SaveButtonType, SaveAsButtonType, ButtonType.OK, ButtonType.Cancel)
}

object ExitController extends ExitController {

  def fireEvent(stage: Option[Stage]): Unit = stage match {
    case Some(s) => s.fireEvent(new WindowEvent(s, WindowEvent.WindowCloseRequest))
    case None => throw new UnknownError
  }

  def handleExitEvent(stage: Stage, event: WindowEvent): Unit = exit(stage) match {
    case SaveText => println(SaveText); event.consume()
    case SaveAsText => println(SaveAsText); event.consume()
    case CancelText => println(CancelText); event.consume()
    case OkText => println("Exit")
    case _ => throw new UnknownError
  }

  def exit(stage: Stage): String = initAlert(stage).showAndWait().getOrElse(throw new IllegalArgumentException).text

  def initAlert(stage: Stage): Alert = new Alert(AlertType.Warning) {
    def initDefaultButton(alert: Alert): Unit = {
      alert.getDialogPane.lookupButton(ButtonType.OK).asInstanceOf[control.Button].setDefaultButton(false)
      alert.getDialogPane.lookupButton(ButtonType.Cancel).asInstanceOf[control.Button].setDefaultButton(true)
    }

    initOwner(stage)
    title = AlertTitle
    headerText = AlertHeader
    contentText = AlertContent
    buttonTypes = ButtonTypes

    initDefaultButton(this)
  }
}

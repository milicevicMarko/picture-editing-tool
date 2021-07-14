package frontend.exit

import backend.io.FileExport
import frontend.main.MainControllerApp
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

  def handleExitEvent(event: WindowEvent): Unit = exit(MainControllerApp.stage) match {
    case SaveText => FileExport.tryToSave(None); println("Save and Exit")
    case SaveAsText => FileExport.tryToSave(Option(MainControllerApp.stage)); println("Save As and Exit")
    case CancelText => event.consume(); println("Cancel")
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

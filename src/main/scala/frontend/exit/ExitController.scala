package frontend.exit

import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.{Stage, WindowEvent}

object Labels {
  val SaveText: String = "Save"
  val SaveAsText: String = "Save As..."
  val CancelText: String = "Cancel"
  val OkText: String = "OK"

  val AlertTitle: String = "Close Confirmation"
  val AlertHeader: String = "Are you sure you want to exit without saving?"
  val AlertContent: String = "All the work done since your last save will be lost."
}

object ExitController {

  val ButtonTypes = Seq(
    new ButtonType(Labels.SaveText), new ButtonType(Labels.SaveAsText), ButtonType.OK, ButtonType.Cancel)

  def fireEvent(stage: Option[Stage]): Unit = stage match {
    case Some(s) => s.fireEvent(new WindowEvent(s, WindowEvent.WindowCloseRequest))
    case None => throw new UnknownError
  }

  def handleExitEvent(stage: Stage, event: WindowEvent): Unit = ExitController.exit(stage) match {
    case Labels.SaveText => println("Save"); event.consume()
    case Labels.SaveAsText => println("Save As..."); event.consume()
    case Labels.CancelText => println("Cancel"); event.consume()
    case Labels.OkText => println("Exit")
    case _ => throw new UnknownError
  }

  def initAlert(stage: Stage): Alert = new Alert(AlertType.Warning) {
    initOwner(stage)
    title = Labels.AlertTitle
    headerText = Labels.AlertHeader
    contentText = Labels.AlertContent
    // todo - change default focus to cancel
    buttonTypes = ButtonTypes
  }

  def exit(stage: Stage): String = initAlert(stage).showAndWait().getOrElse(throw new IllegalArgumentException).text
}

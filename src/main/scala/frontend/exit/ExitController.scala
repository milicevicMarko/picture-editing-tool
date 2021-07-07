package frontend.exit

import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage

object ExitController {
  def close(stage: Option[Stage]): String = stage match {
    case Some(s) => close(s)
    case None => throw new ExceptionInInitializerError()
  }

  def close(stage: Stage): String = {
    val SaveType: ButtonType = new ButtonType("Save")
    val SaveAsType: ButtonType = new ButtonType("Save As...")

    val alert = new Alert(AlertType.Confirmation) {
      initOwner(stage)
      title = "Close Confirmation"
      headerText = "Are you sure you want to exit without saving?"
      contentText = "All the work done since your last save will be lost."

      buttonTypes = Seq(SaveType, SaveAsType, ButtonType.OK, ButtonType.Cancel)
    }

    // todo - change default focus to cancel
    alert.showAndWait().getOrElse(throw new IllegalArgumentException).text
  }

}

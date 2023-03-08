package GUI

import Exceptions.{IllegalFile, InvalidUserInput}
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType

import java.io.{FileNotFoundException, IOException}

class ExceptionHandler {

  def react (e: Exception) = e match {
    case user: InvalidUserInput          => simpleWarning(user.getMessage)
    case file: IllegalFile               => fileSavingWarning(file.getMessage)
    case io: IOException                 => fileReadWriteWarning(io.getMessage)
    case notfound: FileNotFoundException => fileReadWriteWarning(notfound.getMessage)
    case unknown                         => unknownWarning(unknown.getMessage)
  }

  private def simpleWarning (text: String) = {
    val warning = new Alert(AlertType.Warning)
    warning.setHeaderText("Inappropriate User Input")
    warning.setContentText(text)
    warning.show()
  }

  private def fileSavingWarning (text: String) = {
    val warning = new Alert(AlertType.Warning)
    warning.setHeaderText("File error")
    warning.setContentText(text)
    warning.getButtonTypes.clear()
    warning.getButtonTypes.addAll(new ButtonType("Load Main File"), new ButtonType("Load Backup File"), new ButtonType("New Game"))
    (warning.showAndWait()).get.text match {
      case "Load Main File"   => CasinoApp.loadGame("interrupted")
      case "Load Backup File" => CasinoApp.loadGame("backup")
      case "New Game"         => CasinoApp.newGame()
    }
  }

  private def fileReadWriteWarning (text: String) = {
    val warning = new Alert(AlertType.Warning)
    warning.setHeaderText("File error")
    warning.setContentText(text)
    warning.show()
  }

  private def unknownWarning (text: String) = {
    val warning = new Alert(AlertType.Warning)
    warning.setHeaderText("Unknown Error Occur.")
    warning.setContentText(s"Unknown error has occured: ${text}\nRecommended: Quit without saving.")
    warning.getButtonTypes.clear()
    warning.getButtonTypes.addAll(new ButtonType("Quit without saving"), new ButtonType("OK"))
    (warning.showAndWait()).get.text match {
      case "Quit without saving"    => CasinoApp.quitGame("nosave")
      case "OK"                     =>
    }
  }
}

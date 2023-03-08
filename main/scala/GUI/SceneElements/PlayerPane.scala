package GUI.SceneElements
import GUI.SceneHandler
import GameLogic.Player.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.HBox
import scalafx.scene.text.Font

class PlayerPane (player: Player, sceneHandler: SceneHandler) extends HBox {
  val name = new Label(player.name)
  name.setFont(Font.font("Arial Narrow", 12))
  name.setPrefWidth(600)

  val typeName = new Label(if (player.isComputer)"Computer Player" else "Human Player")
  typeName.setFont(Font.font("Arial Narrow", 12))
  typeName.setAlignment(Pos.Center)
  typeName.setMinWidth(200)
  typeName.setPrefWidth(500)

  val removeButton = new Button("Remove")
  removeButton.text = "Remove"
  removeButton.minWidth = 100
  removeButton.setFont(Font.font("Arial Narrow", 12))
  removeButton.onAction = e => sceneHandler.removePlayer(player)

  fillHeight = true
  alignment = Pos.Center
  margin = new Insets(Insets(10,40,0,40))
  this.getChildren.addAll(name,typeName,removeButton)
}

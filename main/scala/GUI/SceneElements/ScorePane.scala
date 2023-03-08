package GUI.SceneElements

import GUI.SceneHandler
import GameLogic.Player.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.HBox
import scalafx.scene.text.Font

class ScorePane (player: Player) extends HBox {
  val name = new Label(player.name)
  name.setFont(Font.font("Arial Narrow", 12))
  name.setPrefWidth(600)

  val score = new Label(player.giveScore.toString)
  score.setFont(Font.font("Arial Narrow", 12))
  score.setAlignment(Pos.Center)
  score.setMinWidth(200)
  score.setPrefWidth(500)

  fillHeight = true
  alignment = Pos.Center
  margin = new Insets(Insets(10,40,0,40))
  this.getChildren.addAll(name,score)
}

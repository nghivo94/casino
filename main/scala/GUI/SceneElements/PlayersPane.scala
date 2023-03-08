package GUI.SceneElements

import GUI.SceneHandler
import GameLogic.Player.Player
import scalafx.scene.layout.VBox

import scala.collection.mutable.Map

class PlayersPane (sceneHandler: SceneHandler) extends VBox {

  def updatePlayers (players: Vector[Player]) = {
    this.getChildren.clear()
    players.foreach( createPlayerPane(_) )
  }

  private def createPlayerPane (player: Player) = this.getChildren.add(new PlayerPane(player,sceneHandler))
}

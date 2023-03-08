package GUI.SceneElements

import GUI.SceneHandler
import GameLogic.Card.Card
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, FlowPane}
import scalafx.scene.paint.Color

class PlayerHandPane (sceneHandler: SceneHandler) extends FlowPane {

  orientation    = Orientation.Horizontal
  prefWrapLength = 400
  prefWidth      = 1000
  hgap           = 15
  vgap           = 15
  alignment      = Pos.Center

  def updateHand (hand: Vector[Card]) = {
    this.getChildren.clear()
    hand.foreach( createCardLabel(_) )
  }

  private def createCardLabel (card: Card) = this.getChildren.add(new CardLabel(card, sceneHandler))
}

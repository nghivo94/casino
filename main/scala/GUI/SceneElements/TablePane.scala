package GUI.SceneElements

import GUI.SceneHandler
import GameLogic.Card.Card
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, FlowPane}
import scalafx.scene.paint.Color

class TablePane (sceneHandler: SceneHandler) extends FlowPane {

  orientation    = Orientation.Horizontal
  prefWrapLength = 600
  hgap           = 10
  vgap           = 10
  prefHeight     = 800
  background     = new Background(Array(new BackgroundFill(Color.web("#2A7A45"), new CornerRadii(0), Insets.Empty)))
  alignment      = Pos.Center

  def updateTable (table: Vector[Card]) = {
    this.getChildren.clear()
    table.foreach( createCardLabel(_) )
  }

  private def createCardLabel (card: Card) = this.getChildren.add(new CardLabel(card, sceneHandler))
}

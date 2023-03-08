package GUI.SceneElements


import GUI.SceneHandler
import GameLogic.Card.Card
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}

class CardLabel (val card: Card, val sceneHandler: SceneHandler) extends Label {
  val imgView = new ImageView(new Image(card.giveImgPath))
  imgView.setFitWidth(60)
  imgView.setFitHeight(100)
  graphic = imgView
  onMouseClicked = e => chooseCard()
  def chooseCard () = sceneHandler.chooseCard(card)
}
package GUI.SceneElements

import scalafx.geometry.Insets
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

class SummaryPane (comments: Vector[String]) extends VBox {

  padding     = Insets(5)
  background  = new Background(Array(new BackgroundFill(Color.web("#DCE796"), new CornerRadii(0), Insets.Empty)))
  prefWidth   = 200
  comments.foreach( createTextNode(_) )

  private def createTextNode (text: String) = {
    val textNode = new Text
    textNode.setText(text)
    textNode.wrappingWidth = 160
    textNode.margin = new Insets(Insets(5,0,10,0))
    this.getChildren.add(textNode)
  }
}

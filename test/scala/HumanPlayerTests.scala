import Exceptions.{InvalidCapture, NoCardInHandChosen, WrongMoveButton}
import GameLogic.Card.Card
import GameLogic.Move.{Capture, Move, Play}
import GameLogic.Player.Player
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HumanPlayerTests extends AnyFlatSpec with Matchers {

  val testName = Some("Vienna")
  val testType = Some("human")
  val testOrder = Some("2")
  val testScore = Some("3")
  val testSweep = Some("0")
  val testHand = Some(Vector(Card("h2"),Card("d0"),Card("ca")))
  val testPileSeen = Some(Vector[Card]())
  val testCaptures = Vector(Vector(Card("s2"),Card("d2"), Card("ha"), Card("sa")), Vector(Card("d3")))

  "Human Player" should "make appropriate move based on cards chosen." in {
    val player = Player(testName,testType,testOrder,testScore,testSweep,testHand,testPileSeen,testPileSeen)
    player.chooseCard(Card("h2"))
    assert(player.makeMove(Some(Play)) === Move(Play,Card("h2"),Vector[Card]()))
    assert(player.giveChosenCards === Vector[Card]())

    player.chooseCard(Card("h2"))
    testCaptures(0).foreach( player.chooseCard(_) )
    assert(player.giveChosenCards === Vector(Card("h2"),Card("s2"),Card("d2"), Card("ha"), Card("sa")))
    assert(player.makeMove(Some(Capture)) === GameLogic.Move.Move(Capture,Card("h2"), Vector(Card("s2"),Card("d2"), Card("ha"), Card("sa"))))
    assert(player.giveChosenCards === Vector[Card]())
  }

  it should "react as planned when making inappropriate moves." in {
    val player = Player(testName,testType,testOrder,testScore,testSweep,testHand,testPileSeen,testPileSeen)

    assertThrows[NoCardInHandChosen]{player.makeMove(Some(Play))}
    player.chooseCard(Card("h8"))
    assertThrows[NoCardInHandChosen]{player.makeMove(Some(Capture))}
    assert(player.giveChosenCards === Vector[Card]())

    player.chooseCard(Card("h2"))
    assertThrows[WrongMoveButton]{player.makeMove(Some(Capture))}
    assert(player.giveChosenCards === Vector[Card]())

    player.chooseCard(Card("h2"))
    player.chooseCard(testCaptures(1)(0))
    assertThrows[InvalidCapture]{player.makeMove(Some(Capture))}
    assert(player.giveChosenCards === Vector[Card]())
  }
}

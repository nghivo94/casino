import Exceptions.{InvalidCapture, NoCardInHandChosen, WrongMoveButton}
import GameLogic.Card.Card
import GameLogic.Move.{Capture, Move, Play}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MoveTests extends AnyFlatSpec with Matchers {

  "Move object" should "create moves with 1st apply function (for computer)." in {
    val playMove = Move(Play, Card("h2"), Vector[Card]())
    assert(playMove.moveType === Play)
    assert(playMove.cardPlayed === Card("h2"))
    assert(playMove.cardsCaptured === Vector())

    val captureMove = Move(Capture, Card("h2"), Vector(Card("s2"), Card("d2")))
    assert(captureMove.moveType === Capture)
    assert(captureMove.cardPlayed === Card("h2"))
    assert(captureMove.cardsCaptured === Vector(Card("s2"), Card("d2")))
  }

  it should "create moves with 2nd apply function (for human)." in {
    assert(Move(Play, Some(Card("h2")), Vector[Card]()) === Move(Play, Card("h2"), Vector[Card]()))
    assert(Move(Capture, Some(Card("h2")), Vector(Card("s2"), Card("d2"))) === Move(Capture, Card("h2"), Vector(Card("s2"), Card("d2"))))
    assert(Move(Capture, Some(Card("h2")), Vector(Card("s2"), Card("d2"), Card("ca"), Card("sa"))) === Move(Capture, Card("h2"), Vector(Card("s2"),Card("d2"), Card("ca"), Card("sa"))))
    assert(Move(Capture, Some(Card("d0")), Vector(Card("d9"), Card("ca"), Card("c6"))) === Move(Capture, Card("d0"), Vector(Card("d9"), Card("ca"), Card("c6"))))
    assert(Move(Capture, Some(Card("ca")), Vector(Card("s7"), Card("h0"), Card("d7"), Card("c4"))) === Move(Capture, Card("ca"), Vector(Card("s7"), Card("h0"), Card("d7"), Card("c4"))))
  }

  it should "detect exceptions with 2nd apply function (for human)." in {
    assertThrows[NoCardInHandChosen] {Move(Play, None, Vector[Card]())}
    assertThrows[NoCardInHandChosen] {Move(Capture, None, Vector[Card]())}

    assert(intercept[WrongMoveButton]{Move(Play, Some(Card("h2")), Vector(Card("s2")))}.text === "Wrong move button. User should use 'Capture' button.")
    assert(intercept[WrongMoveButton]{Move(Play, Some(Card("h2")), Vector(Card("s3")))}.text === "Wrong move button. User should use 'Capture' button.")
    assert(intercept[WrongMoveButton]{Move(Capture, Some(Card("h2")), Vector[Card]())}.text === "Wrong move button. User should use 'Play' button.")

    assertThrows[InvalidCapture] {Move(Capture, Some(Card("h2")), Vector(Card("s3")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("h2")), Vector(Card("s2"), Card("d2"), Card("s3")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("h2")), Vector(Card("sa"), Card("ca"), Card("da")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("d0")), Vector(Card("d8"), Card("ca"), Card("c6")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("ca")), Vector(Card("s7"), Card("d4"), Card("h0"), Card("c4")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("ca")), Vector(Card("sa")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("h2")), Vector(Card("s4")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("s2")), Vector(Card("s0")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("h9")), Vector(Card("s0"), Card("ca"), Card("s7")))}
    assertThrows[InvalidCapture] {Move(Capture, Some(Card("h9")), Vector(Card("s0"), Card("ca"), Card("s7"), Card("d4"), Card("s5")))}
}

}

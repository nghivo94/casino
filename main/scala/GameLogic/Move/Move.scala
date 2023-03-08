package GameLogic.Move

import Exceptions.{InvalidCapture, NoCardInHandChosen, WrongMoveButton}
import GameLogic.Card.Card

//After its creation, the case class Move is only used to package its components.
case class Move (moveType: MoveType, cardPlayed: Card, cardsCaptured: Vector[Card])

object Move {
  //This function allows straightforward creation of Move instances.
  def apply (moveType: MoveType, cardPlayed: Card, cardsCaptured: Vector[Card])       : Move = new Move(moveType, cardPlayed, cardsCaptured)

  //This function creates and verifies the moves.
  def apply (moveType: MoveType, cardChosen: Option[Card], cardsChosen: Vector[Card]) : Move = {
    //The function also detects elementary mistakes, such as no card in hand is chosen, or wrong move buttons.
    val cardPlayed = cardChosen match {
      case Some(value) => value
      case None        => throw NoCardInHandChosen("No card in hand was chosen.")
    }
    val cardsCaptured = cardsChosen
    moveType match { //Use separate verification for different move types.
      case Play    => verifyPlay(cardsChosen)
      case Capture => verifyCapture(cardPlayed,cardsChosen)
    }
    //The move is created if no exception is thrown.
    new Move(moveType, cardPlayed, cardsCaptured)
  }

  private def verifyPlay    (cardsChosen: Vector[Card])                   : Unit = if (cardsChosen.nonEmpty) throw WrongMoveButton("Wrong move button. User should use 'Capture' button.")
  private def verifyCapture (cardPlayed: Card, cardsChosen: Vector[Card]) : Unit = {
    if (cardsChosen.isEmpty) throw WrongMoveButton("Wrong move button. User should use 'Play' button.")
    cardsChosen.filter( _.numVal != cardPlayed.valueInHand) match { //Filter cards with same value with played card for efficiency.
      case cardsCaptured if cardsCaptured.nonEmpty => { //If there are remaining cards, continue checking
        //Simple checking if the following condition holds:
            //Value in hand of card played must be higher than max value of cards captured.
            //Sum of values of cards captured must be divisible by value of card played.
        (cardsCaptured.map( _.numVal).sum % cardPlayed.valueInHand,cardsCaptured.map( _.numVal).max > cardPlayed.valueInHand) match {
          case (0,false)     => strongVerification (cardPlayed, cardsChosen) //If these conditions passed, continue checking
          case other         => throw InvalidCapture("Invalid capture.") //Throw exception if not passed.
        }
      }
      case valid => //If all cards captured has same value with card played, pass the verification.
    }
  }

  //This function conducts a strong verification should all simple conditions are passed.
  //It finds all possible moves with the card played and cards chosen,
    //then check if there is a move that captures all cards chosen.
    //If not, the move does not pass verification. Throw exception.
  private def strongVerification (cardPlayed: Card, cardsChosen: Vector[Card]) : Unit = if (
    (new Finder(cardsChosen, Vector(cardPlayed))).findAllMoves()
    .forall(_.cardsCaptured.length != cardsChosen.length)) throw InvalidCapture("Invalid Capture.")
}
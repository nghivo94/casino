package GameLogic.Player

import GameLogic.Card.Card
import GameLogic.Move.{Move, MoveType}

class HumanPlayer (name: String, order: Int, score: Int = 0, sweep: Int = 0,
                   hand: Vector[Card] = Vector[Card](), pile: Vector[Card] = Vector[Card](), seen: Vector[Card] = Vector[Card]())
  extends Player (name, "human", order, score, sweep, hand, pile, seen) {

  //The isComputer always returns false for this subclass.
  def isComputer: Boolean = false

  //The function makes and returns a move based on cards chosen.
  def makeMove (opMoveType: Option[MoveType], table: Vector[Card] = Vector[Card]()) = {
    //Storing the cards chosen ...
    val cardChosen   = this.cardChosen
    val cardsChosen  = this.cardsChosen
      //before reseting the cards chosen variables so that when a move is made, valid or not, the cards chosen in always reset.
    this.cardChosen  = None
    this.cardsChosen = Vector()
    //Uses the apply function with verification, if valid returns a move, else throw exceptions.
    Move(opMoveType.get, cardChosen, cardsChosen)
  }
}

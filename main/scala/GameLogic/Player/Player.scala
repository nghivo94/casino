package GameLogic.Player

import Exceptions.{IllegalPlayerInfo, MissingPlayerInfo, NoNameChosen}
import GameLogic.Card.Card
import GameLogic.Move.{Capture, Finder, Move, MoveType}

import scala.util.Random.nextInt

abstract class Player (val name: String, val typeName: String, //The player has 2 public val, name and type of player
                       protected var order: Int, //The order of player in the Game, unchanged when the game is started, used for file reading.
                          //other necessary variables:
                       protected var score: Int, protected var sweep: Int,
                       protected var hand: Vector[Card], protected var pile: Vector[Card], protected var seen: Vector[Card]) {

  //Card and cards Chosen, used by human players
  protected var cardChosen  : Option[Card] = None       //Card Chosen is card in hand chosen, only 1 such card can exist at a time
  protected var cardsChosen : Vector[Card] = Vector()   //Cards Chosen are cards on table chosen (if any)

  //abstract functions that needs to be implemented in subclasses
  def isComputer : Boolean
  def makeMove (opMoveType: Option[MoveType], table: Vector[Card] = Vector[Card]()): Move

  //Effect-free functions for extracting information.
  def giveOrder : Int = order
  def giveScore : Int = score

  def giveHand : Vector[Card] = hand
  def givePile : Vector[Card] = pile
  def giveSeen : Vector[Card] = seen
  def giveChosenCards : Vector[Card] = cardChosen match {
    case Some(card) => Vector(card) ++ cardsChosen
    case None       => cardsChosen
  }
  def giveSaveDetails : Tuple8[String, String, Int, Int, Int,
                               Vector[Card],Vector[Card],Vector[Card]] = (name,typeName,order,score,sweep,hand,pile,seen)

  //Check if the current player has any cards left, used to determine if a round has ended.
  def hasCards : Boolean = hand.nonEmpty

  //Assign an order, used at the start of the game.
  def assignOrder (assignedOrder: Int) : Unit = order = assignedOrder

  //Updating the variables in restricted manners.
  def addScore ()     : Unit = score += 1
  def addSweep ()     : Unit = sweep += 1
  def sweepToScore () : Unit = score += sweep

  def addHand    (cards: Vector[Card]) : Unit = {
    hand ++= cards
    addSeen (cards) //When new cards are added to hand, the player would also "see" the cards
  }
  def removeHand (card: Card)          : Unit = hand = hand.filter( _ != card)

  def addPile (cards: Vector[Card]) : Unit = pile ++= cards
  def addSeen (cards: Vector[Card]) : Unit = {
    seen ++= cards
    seen = seen.distinct
  }

  //This function chooses a card.
  def chooseCard (card: Card) : Unit = {
    if      (cardsChosen.contains(card)) cardsChosen = cardsChosen.filter( _ != card ) //If that card is already chosen, remove the card from chosen cards
    else if (cardChosen.contains(card))  cardChosen  = None                            //Similarly, resetting the cardChosen
    else if (hand.contains(card))        cardChosen  = Some(card)      //Else if the card is on the hand, setting cardChosen to be the new card
    else                                 cardsChosen ++= Vector(card)  //Else add the card to the cards chosen on table.
  }

  //This function resets the information of player regarding a specific round.
  def resetRoundInfo () : Unit = {
    sweep = 0
    hand = Vector[Card]()
    pile = Vector[Card]()
    seen = Vector[Card]()
  }

  //This function resets the information of player regarding a specific game.
  def resetGameInfo ()     : Unit = {
    score = 0
    order = 0
  }

  //This function chooses the cards that form one possible move, preferably a capture move.
  def showHints (table: Vector[Card]) : Unit = {
    //Reseting the chosen cards
    cardsChosen = Vector()
    cardChosen  = None
    (new Finder(table, hand)).findAllMoves().filter( _.moveType == Capture ) match { //Find all possible capture moves (if any)
      case nonEmpty if nonEmpty.nonEmpty => {val randomMove = nonEmpty(nextInt(nonEmpty.length)) //If there are capture moves, choose a random move
                                            (Vector(randomMove.cardPlayed) ++ randomMove.cardsCaptured).foreach( chooseCard(_) )} //Chooses the cards accordingly
      case empty                         => chooseCard(hand(nextInt(hand.length))) //Else, choose a random card in hand.
    }
  }
}

object Player {

  //This function is used for creating a player from the file, detecting possible missing values and invalid values.
  def apply (opName: Option[String], opTypeName: Option[String], opOrder: Option[String], opScore: Option[String], opSweep: Option[String],
             opHand: Option[Vector[Card]], opPile: Option[Vector[Card]], opSeen: Option[Vector[Card]]) : Player = {

    val name     = obtainValue[String](opName,     "Missing player's name.")
    val typeName = obtainValue[String](opTypeName, "Missing player's type.")
    val order = obtainNumValue(opOrder, ("Invalid player's order", "Missing player's order"))
    val score = obtainNumValue(opScore, ("Invalid player's score","Missing player's score."))
    val sweep = obtainNumValue(opSweep, ("Invalid player's number of sweeps","Missing player's number of sweeps."))
    val hand  = opHand match {
      case Some(valid) if valid.size < 5 => valid
      case Some(invalid) => throw IllegalPlayerInfo("Invalid player's hand. Cards on player's hand exceed 4.")
      case None          => throw MissingPlayerInfo("Missing player's cards on hand.")
    }
    val pile = obtainValue[Vector[Card]](opPile, "Missing player's cards in pile.")
    val seen = obtainValue[Vector[Card]](opSeen, "Missing player's cards seen.")
    typeName match {
      case "human"  => new HumanPlayer(name,order,score,sweep,hand,pile,seen) //Straightforward creation for human players
      case computer if (computer.endsWith("computer")) => ComputerPlayer(name,computer,order,score,sweep,hand,pile,seen) //Outsourcing creation of Computer players
      case other    => throw IllegalPlayerInfo(s"Invalid player's type: ${other}.")
    }
  }

  //These functions are used to create players with and without a name.
  def apply (name: String, typeName: String, order: Int, mode: String) : Player = typeName match {
    case "human"    => new HumanPlayer(name,order)
    case "computer" => ComputerPlayer(mode,order,name)
  }
  def apply (typeName: String, order: Int, mode: String)               : Player = typeName match {
    case "human"    => throw NoNameChosen("User should choose a name for human player.")
    case "computer" => ComputerPlayer(mode, order)
  }

  //These are helpers functions for checking missing / invalid player information
  private def obtainValue [T] (opValue: Option[T], message: String) : T = opValue match {
    case Some(value) => value
    case None => throw MissingPlayerInfo(message)
  }
  private def obtainNumValue (opValue: Option[String], messages: (String,String)) : Int = opValue match {
    case Some(number) if number.toIntOption.nonEmpty && number.toInt >= 0 => number.toInt
    case Some(other) => throw IllegalPlayerInfo(s"${messages._1}: ${other}.")
    case None        => throw MissingPlayerInfo(s"${messages._2}")
  }
}
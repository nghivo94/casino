package GameLogic

import GameLogic.Card.{Card, CardDeck, Spade, Table}
import GameLogic.Move.{Capture, Move, Play}
import GameLogic.Player.{ComputerPlayer, HumanPlayer, Player}

class Round (val cardDeck: CardDeck,val table: Table,private val players: Vector[Player], //Values passed on by Game class
  private var currentTurn: Int = 1, //Initial index for current turn is always 1, as the Dealer of a round always has index 0
  private var currentView: Int = 1,
  private var currentLastCapture: Option[Int] = None,
  private var currentComments: Vector[String] = Vector()) {

  //Giving access to some private vars and vals
  def giveCurrentTurn : Player = players(currentTurn)
  def giveCurrentView : Player = players(currentView)
  def giveCurrentComments : Vector[String] = currentComments
  def giveCurrent : Tuple4[Int, Int, Option[Int], Vector[String]] = (currentTurn,currentView,currentLastCapture,currentComments)

  //Check if the Round is complete by checking if the current turn has card
  def isComplete : Boolean = !giveCurrentTurn.hasCards

  //The function to start a round
  def startRound () : Unit = {
    //Resetting all the previous round (if any) information
    players.foreach( _.resetRoundInfo() )
    cardDeck.newDeck()
    table.resetTable()

    //Dealing each player 2 cards, then 2 cards on table, and repeat
    for (i <- 0 to 1) {
      players.tail.foreach( player => player.addHand(cardDeck.drawTwoCards()) )
      table.addCards(cardDeck.drawTwoCards())
      players.head.addHand(cardDeck.drawTwoCards())
    }
    players.foreach( _.addSeen(table.giveTable) ) //Updating seen cards (on table)
    currentView = players.indexOf((players.tail ++ Vector(players.head)).find( !_.isComputer ).get) //The current player who has the view is the first human player in turn.
    currentComments ++= Vector("A new round has started!",
      s"${players.head.name} is the dealer of this round.",
      s"Initial cards on table are ${table.giveTable.map( _.giveName ).mkString(", ")}.") //Update comments
    runComPlayer() //Automatically run the computer players.
  }

  //This function takes in a move and response to that move.
  def responseMove (move: Move) : Vector[Card] = {
    val player = giveCurrentTurn        //For convenient later use, store the current player in turn in a val
    //Distributing the cards and updating comments.
    player.removeHand(move.cardPlayed)
    move.moveType match {
      case Play => {
        table.addCards(Vector(move.cardPlayed))
        currentComments ++= Vector(s"Player ${player.name} played ${move.cardPlayed.giveName}.")
      }
      case Capture => {
        table.removeCards(move.cardsCaptured)
        player.addPile(Vector(move.cardPlayed) ++ move.cardsCaptured)
        currentLastCapture = Some(currentTurn)
        currentComments ++= Vector(s"Player ${player.name} played ${move.cardPlayed.giveName} and captured ${move.cardsCaptured.map( _.giveName ).mkString(", ")}.")
      }
    }
    player.addHand(cardDeck.drawACard())
    players.foreach( _.addSeen(Vector(move.cardPlayed)) ) //Update seen value (card played) for all players
    if (table.hasSweep) {
      player.addSweep()
      currentComments ++= Vector(s"Player ${player.name} had a sweep!")
    } //Check and add a sweep if the player has made a sweep.
    val updatedHand = giveCurrentView.giveHand    //Store the updated hand of the current player who has just finished their move.
    //Updating current turn and current view.
    currentTurn = (currentTurn + 1)%players.length
    currentView = if (giveCurrentTurn.isComputer) currentView else currentTurn //Only human player should have the view.
    runComPlayer() //Automatically run the computer players.
    updatedHand    //Returns the updated hand for the last player in view to know their updated hand before moving to the next player who has the view.
  }

  private def runComPlayer () : Unit = players(currentTurn) match {
    case human   : HumanPlayer    =>
                                     //If the round is incomplete, automatically make and response to computer player's move.
    case computer: ComputerPlayer => if (!isComplete) responseMove(computer.makeMove(None,table.giveTable))
  }

  //This function is used to end a round.
  def endRound () : Unit = {
    if (currentLastCapture.nonEmpty) players(currentLastCapture.get).addPile(table.giveTable) //Give the remaining cards on table to the player who made the last capture move.
    //Scores are given to the players with maximum number of cards and spades cards are found in this way in case there are 2 or more such players.
    val maxCards  = players.map( _.givePile.size ).max
    val maxSpades = players.map( _.givePile.count( _.suit == Spade ) ).max
    players.filter( player => player.givePile.size == maxCards ).foreach( _.addScore() )
    players.filter( player => player.givePile.count( _.suit == Spade ) == maxSpades ).foreach( player => {player.addScore()
                                                                                                          player.addScore()} )
    //Give scores based on special cards in the player's pile.
    players.foreach( player => player.givePile.foreach {
      case Card(_,_,_,14) => player.addScore()
      case Card(_,_,_,15) => player.addScore()
      case Card(_,_,_,16) => {player.addScore()
                              player.addScore()}
      case other =>
    })
    players.foreach( _.sweepToScore() )   //Adding the sweeps (if any) of each player to scores

    //Reset information about the current round.
    players.foreach( _.resetRoundInfo() )
    cardDeck.newDeck()
    table.resetTable()
  }
}
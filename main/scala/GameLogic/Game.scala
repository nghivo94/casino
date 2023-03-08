package GameLogic

import Exceptions.{IllegalPlayerInfo, InvalidNumPlayers, MissingCard, MissingMetaData, NoHumanPlayer}
import GameLogic.Card.{Card, CardDeck, Table}
import GameLogic.Player.Player

class Game {
  val cardDeck    = new CardDeck
  val table       = new Table
  val fileManager = new FileManager(this)

  private var players : Vector[Player] = Vector()
  private var winners : Vector[Player] = Vector()
  private var started : Boolean = false

  private var currentRound : Option[Round] = None
  private var currentDealer = 0

  private var currentTurn   = 1
  private var currentView   = 1
  private var currentLastCapture : Option[Int] = None
  private var currentComments : Vector[String] = Vector()

  def hasStarted  : Boolean = started
  def singleHuman : Boolean = players.count(!_.isComputer) == 1

  def givePlayers : Vector[Player] = players
  def giveWinners : Vector[Player] = winners
  def giveRound   : Option[Round]  = currentRound

  def loadMetaData (opDeck: Option[Vector[Card]], opTable: Option[Vector[Card]], opDeal: Option[Int],
                    opTurn: Option[Int], opView: Option[Int], opCapture: Option[Option[Int]]) : Unit = {
    cardDeck.loadDeck (opDeck)
    table   .loadTable(opTable)
    currentDealer      = matchOption(opDeal,     "Missing current dealer.")
    currentTurn        = matchOption(opTurn,     "Missing current turn.")
    currentView        = matchOption(opView,     "Missing current view.")
    currentLastCapture = matchOption(opCapture,  "Missing current last capture.")
  }
   def loadComments (comments: Vector[String]) = currentComments = comments
  private def matchOption [T] (source: Option[T], errorMessage: String) : T = source match {
    case Some(value) => value
    case None => throw MissingMetaData(errorMessage)
  }
  def loadPlayers (players: Vector[Player]) : Unit = {
    val orderedPlayers = (players.map( player => (player,player.giveOrder))).sortBy( _._2 )
    if (orderedPlayers.map( _._2 ).distinct.length != orderedPlayers.map( _._2 ).length ) throw IllegalPlayerInfo("Abnormal Player's order.")
    if (orderedPlayers(orderedPlayers.length - 1)._2 != orderedPlayers.length - 1)        throw IllegalPlayerInfo("Abnormal Player's order / number.")
    val checkResults = abnormalNumPlayer(orderedPlayers.map( _._1))
    if (checkResults._1 || checkResults._2 || checkResults._3) throw IllegalPlayerInfo("Abnormal Number of Players in file.")
    this.players = orderedPlayers.map( _._1 )
  }

  private def createRoundPlayers : Vector[Player] = players.slice(currentDealer,players.length) ++ players.slice(0,currentDealer)

  def newRound ()    : Unit = {
    if (!started) (players.zipWithIndex).foreach( pair => pair._1.assignOrder(pair._2))
    val checkResult = abnormalNumPlayer(players)
    if (checkResult._1) throw NoHumanPlayer("No human player was added.")
    if (checkResult._2) throw InvalidNumPlayers("Not enough players were added.")
    if (checkResult._3) throw InvalidNumPlayers("Too many players was added.")
    currentRound = Some(new Round(cardDeck,table,createRoundPlayers))
    currentRound.get.startRound()
    started = true
  }

  def loadRound ()   : Unit = {
    var totalCards = table.giveTable ++ cardDeck.giveDeck
    players.foreach( player => totalCards ++= player.giveHand ++ player.givePile )
    if (totalCards.distinct.length != 52) throw MissingCard("Missing cards.")
    new Round(cardDeck,table,createRoundPlayers,
      currentTurn = currentTurn,currentView = currentView,
      currentLastCapture = currentLastCapture,currentComments  = currentComments) match {
        case inComplete if !inComplete.isComplete => currentRound = Some(inComplete)
        case complete                             => newRound()
      }
    started = true
  }

  def endRound ()    : Unit = {
    currentRound = None
    players.filter( _.giveScore >= 16 ) match {
      case nonEmpty if nonEmpty.nonEmpty => winners = nonEmpty.filter( player => player.giveScore == nonEmpty.map( _.giveScore ).max )
      case other =>
    }
    currentDealer = (currentDealer+1)%players.length
  }

  def restartGame () : Unit = {
    currentRound    = None
    currentDealer   = 0
    currentTurn     = 1
    currentView     = 1
    currentComments = Vector()

    players.foreach( _.resetRoundInfo() )
    players.foreach( _.resetGameInfo() )
    cardDeck.newDeck()
    table.resetTable()

    started = false
  }

  def addPlayer (name: String, typeName: String, mode: String) = name match {
    case noName if noName.trim == "" => players ++= Vector(Player(typeName,players.length,mode.trim.toLowerCase))
    case other                       => players ++= Vector(Player(name.trim,typeName,players.length,mode.trim.toLowerCase))
  }

  def removePlayer (player: Player) = players = players.filter( _ != player )

  private def abnormalNumPlayer (players: Vector[Player]) = (players.forall(_.isComputer),players.length < 2,players.length > 12)

  def giveSaveDetails : Tuple7[Vector[Card], Vector[Card], Int, Int, Int, Option[Int], Vector[String]]= {
    if (currentRound.nonEmpty) {
      currentTurn        = currentRound.get.giveCurrent._1
      currentView        = currentRound.get.giveCurrent._2
      currentLastCapture = currentRound.get.giveCurrent._3
      currentComments    = currentRound.get.giveCurrent._4
    }
    (cardDeck.giveDeck,table.giveTable,currentDealer,currentTurn,currentView,currentLastCapture,currentComments)
  }
}
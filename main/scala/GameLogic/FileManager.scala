package GameLogic

import Exceptions.{IllegalExpression, InterruptedFile, MissingMetaData, NoPreviousGame}
import GameLogic.Card.Card
import GameLogic.Player.Player

import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter}
import scala.collection.mutable.Set

class FileManager (game: Game) {

  private val mainFileSrc   : String = "src/main/scala/PreviousGame"
  private val backupFileSrc : String = "src/main/scala/BackupGame"

  private def loadGame (filePath: String, allowInterrupted: Boolean = false) : Unit = {
    val players : Set[Player]   = Set()
    var metaLoaded    : Boolean = false
    var commentLoaded : Boolean = false
    val linesIn = new BufferedReader(new FileReader(filePath))
    var line = linesIn.readLine()
    if (line == null) throw NoPreviousGame("No previous Game / Previous Game is already finishied.")
    line = line.toLowerCase.trim
    if (!line.startsWith("casino") && !line.endsWith("save file")) throw IllegalExpression("Unrecognized file format.")
    line = linesIn.readLine()
    if (line.trim.toLowerCase == "interrupted" && !allowInterrupted) throw InterruptedFile("The game is previously interrupted (exited inappropriately).")
    line = linesIn.readLine()
    while (line != null) {
      line = line.trim.toLowerCase match {
        case "#game metadata"                      => {metaLoaded = true
                                                       readMetaData(game,linesIn)}
        case "#comments"                           => {commentLoaded = true
                                                       readComments(game,linesIn)}
        case player if player startsWith "#player" => readPlayer(players,linesIn)
        case empty if empty.trim.isEmpty           => linesIn.readLine()
        case other                                 => throw IllegalExpression("Illegal expression in file.")
      }
    }
    linesIn.close()
    if (!metaLoaded) throw MissingMetaData ("Missing Meta Data Block.")
    if (!commentLoaded) throw MissingMetaData("Missing current comments.")
    game.loadPlayers(players.toVector)
    game.loadRound()
  }

  private def readMetaData (game: Game, linesIn: BufferedReader) : String = {
    var cardsInDeck: Option[Vector[Card]]         = None
    var cardsOnTable: Option[Vector[Card]]        = None
    var currentTurn: Option[Int]                  = None
    var currentView: Option[Int]                  = None
    var currentDealer: Option[Int]                = None
    var currentLastCapture: Option[Option[Int]]   = None

    var line = linesIn.readLine()
    while (line != null && !line.trim.startsWith("#")) {
			if (line.trim.nonEmpty) {
				if (!line.contains(":")) {
          throw IllegalExpression("Illegal expression in file.")
        }
        val parts = line.split(":")
				parts(0).trim.toLowerCase match {
          case "cardsindeck"        => cardsInDeck   = Some(readCards(parts(1).trim))
          case "cardsontable"       => cardsOnTable  = Some(readCards(parts(1).trim))
          case "currentturn"        => currentTurn   = parts(1).trim.toIntOption
          case "currentview"        => currentView   = parts(1).trim.toIntOption
          case "currentdealer"      => currentDealer = parts(1).trim.toIntOption
          case "currentlastcapture" => currentLastCapture = parts(1).trim match {
            case "" => Some(None)
            case number if number.toIntOption.nonEmpty => Some(number.toIntOption)
            case other => None
          }
          case other => throw IllegalExpression("Illegal expression in file.")
        }
      }
      line = linesIn.readLine()
    }
    game.loadMetaData(cardsInDeck,cardsOnTable,currentDealer,currentTurn,currentView,currentLastCapture)
    line
  }

  private def readPlayer (players: Set[Player], linesIn: BufferedReader) : String = {
    var name: Option[String]       = None
    var typeName: Option[String]   = None
    var order: Option[String]      = None
    var score: Option[String]      = None
    var sweep: Option[String]      = None
    var hand: Option[Vector[Card]] = None
    var pile: Option[Vector[Card]] = None
    var seen: Option[Vector[Card]] = None

    var line = linesIn.readLine()
    while (line != null && !line.trim.startsWith("#")) {
			if (line.trim.nonEmpty) {
				if (!line.contains(":")) {
          throw IllegalExpression("Illegal expression in file.")
        }
        val parts = line.split(":")
				parts(0).trim.toLowerCase match {
          case "name"     => name     = Some(parts(1).trim)
          case "typename" => typeName = Some(parts(1).trim.toLowerCase)
          case "order"    => order    = Some(parts(1).trim)
          case "score"    => score    = Some(parts(1).trim)
          case "sweep"    => sweep    = Some(parts(1).trim)
          case "hand"     => hand     = Some(readCards(parts(1).trim))
          case "pile"     => pile     = Some(readCards(parts(1).trim))
          case "seen"     => seen     = Some(readCards(parts(1).trim))
          case other => throw IllegalExpression("Illegal expression in file.")
        }
      }
      line = linesIn.readLine()
    }
    players.add(Player(name,typeName,order,score,sweep,hand,pile,seen))
    line
  }

  private def readCards (input: String) : Vector[Card] = {
    var cursor = 0
    var cards  = Vector[Card]()
    while (cursor <= input.length - 2) {
      cards ++= Vector(Card(input(cursor).toString.toLowerCase + input(cursor+1).toString.toLowerCase))
      cursor += 2
    }
    cards.distinct
  }

  private def readComments (game: Game, linesIn: BufferedReader) = {
    var comments : Vector[String] = Vector()
    var line = linesIn.readLine()
    while (line != null && !line.trim.startsWith("#")) {
			if (line.trim.nonEmpty) {
        comments++= Vector(line)
      }
      line = linesIn.readLine()
    }
    game.loadComments(comments)
    line
  }

  private def saveGame (filePath: String) : Unit = {
    var saveDetails = Vector(
      "CASINO Save file",
      "",
      "#game metadata",
      s"CardsInDeck: ${cardsText(game.giveSaveDetails._1)}",
      s"CardsOnTable: ${cardsText(game.giveSaveDetails._2)}",
      s"CurrentDealer: ${game.giveSaveDetails._3}",
      s"CurrentTurn: ${game.giveSaveDetails._4}",
      s"CurrentView: ${game.giveSaveDetails._5}",
      s"CurrentLastCapture: ${game.giveSaveDetails._6 match {
        case Some(value) => value.toString
        case None => ""
      }}", "",
      "#comments")
    game.giveSaveDetails._7.foreach( saveDetails ++= Vector(_) )
    saveDetails ++= Vector("")
    game.givePlayers.foreach( player => saveDetails ++= playerText(player) )
    if (game.giveWinners.nonEmpty || !game.hasStarted) saveDetails = Vector()
    val writer = new BufferedWriter(new FileWriter(filePath))
    for (line <- saveDetails) writer.write(line + "\n")
    writer.close()
  }

  private def playerText (player: Player) : Vector[String] = Vector(
    "#player",
    s"Name: ${player.giveSaveDetails._1}",
    s"TypeName: ${player.giveSaveDetails._2}",
    s"Order: ${player.giveSaveDetails._3}",
    s"Score: ${player.giveSaveDetails._4}",
    s"Sweep: ${player.giveSaveDetails._5}",
    s"Hand: ${cardsText(player.giveSaveDetails._6)}",
    s"Pile: ${cardsText(player.giveSaveDetails._7)}",
    s"Seen: ${cardsText(player.giveSaveDetails._8)}", "")

  private def cardsText (cards: Vector[Card]) : String = cards.map( _.giveSaveDetail ).mkString

  def safeSaveGame ()        : Unit = {
    saveGame(mainFileSrc)
    saveGame(backupFileSrc)
  }
  def safeNoSaveGame ()      : Unit = rewriteInterruption(interrupted = false)
  def interruptedSaveGame () : Unit = {
    rewriteInterruption(interrupted = true)
    saveGame(backupFileSrc)
  }

  def mainLoadGame ()        : Unit = loadGame(mainFileSrc)
  def backupLoadGame ()      : Unit = loadGame(backupFileSrc)
  def interruptedLoadGame () : Unit = loadGame(mainFileSrc, allowInterrupted = true)

  private def rewriteInterruption (interrupted: Boolean) : Unit = {
    var previousLines = Vector[String]()
    val linesIn = new BufferedReader(new FileReader(mainFileSrc))
    var lineIn  = linesIn.readLine()
    while (lineIn != null) {previousLines ++= Vector(lineIn)
                            lineIn = linesIn.readLine()}
    linesIn.close()
    if (previousLines.length > 2) previousLines = Vector(
      "CASINO Save file",
      if (interrupted) "Interrupted" else "") ++ previousLines.tail.tail
    val writer = new BufferedWriter(new FileWriter(mainFileSrc))
    for (line <- previousLines) writer.write(line + "\n")
    writer.close()
  }
}

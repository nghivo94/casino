package GUI

import GameLogic.Card.Card
import GameLogic.Move.MoveType
import GameLogic.Player.Player
import GameLogic._

class GameHandler {
  private var game        = new Game
  private var hasPrevious = false

  def newGame () = game = new Game

  def addPlayer (name: String, typeName: String, mode: String) = game.addPlayer(name,typeName,mode)
  def removePlayer (player: Player)                            = game.removePlayer(player)

  def newRound () = {
    game.newRound()
    hasPrevious = false
  }

  def restartGame () = game.restartGame()

  def chooseCard (card: Card): Vector[Card] = {
    val currentTurn = checkTurn._2
    if (checkTurn._1) {
      currentTurn.chooseCard(card)
      currentTurn.giveChosenCards
    }
    else Vector[Card]()
  }
  def showHints () = {
    checkTurn._2.showHints(giveTable)
    checkTurn._2.giveChosenCards
  }
  def makeMove (moveType: MoveType) = {
    val currentTurn = checkTurn._2
    if (checkTurn._1) {
      val updatedHand = game.giveRound.get.responseMove(currentTurn.makeMove(Some(moveType)))
      hasPrevious = true
      if (game.giveRound.get.isComplete) {
        game.giveRound.get.endRound()
        game.endRound()
        CasinoApp.completeRound()
      }
      else CasinoApp.responseMove(updatedHand)
    }
  }

  def notiMode = if (game.singleHuman) "single" else {if (hasPrevious) "next" else "start"}

  def giveWinners  = game.giveWinners
  def givePlayers  = game.givePlayers

  def giveTurnName = game.giveRound.get.giveCurrentTurn.name
  def giveViewName = game.giveRound.get.giveCurrentView.name
  def giveComments = game.giveRound.get.giveCurrentComments
  def giveTable    = game.table.giveTable
  def giveHand     = game.giveRound.get.giveCurrentView.giveHand

  def saveGame (saveMode: String) = saveMode match {
    case "save"        => game.fileManager.safeSaveGame()
    case "nosave"      => game.fileManager.safeNoSaveGame()
    case "interrupted" => game.fileManager.interruptedSaveGame()
  }
  def loadGame (loadMode: String) = loadMode match {
    case "main"        => game.fileManager.mainLoadGame()
    case "interrupted" => game.fileManager.interruptedLoadGame()
    case "backup"      => game.fileManager.backupLoadGame()
  }
  private def checkTurn = (game.giveRound.get.giveCurrentView == game.giveRound.get.giveCurrentTurn, game.giveRound.get.giveCurrentTurn)
}

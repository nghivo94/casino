package GUI

import GameLogic.Card.Card
import GameLogic.Move.MoveType
import GameLogic.Player.Player
import scalafx.application.JFXApp


object CasinoApp extends JFXApp {

  private val instructions = Map(
    "rules" -> ("Rules", "Game Rules", "The current game complies with Finnish / Nordic Casino rules.",
    Vector(
      "1. The Deal:",
      "The dealer is picked at the start of the game. " +
      "The cards are then dealt in pairs, one pair to each player, one on the table (face-up) and one to the dealer, then repeat. " +
      "Then for each round, the player to the left (clockwise) becomes the dealer. " +
      "The game starts with the player to the left of the dealer making the first move, then play turn passes clockwise.",
      "2. Play:",
      "After the dealing, each player has 4 cards (not visible to other players) and 4 cards on the table (visible to all players). " +
      "Each turn, the player always plays one card to the table. " +
      "If the player captures a card or cards, the player put the card played and the card (s) captured in a separate pile. " +
      "If no card is captured, the player plays his or her card on the table face up to be captured later. " +
      "A played card can capture: " +
      "\n     a. A card on the table of the same capture value, or " +
      "\n     b. A set of cards on the table whose capture values add up to the capture value of the played card, or" +
      "\n     c. Several cards or sets of cards that satisfy conditions 1 and/or 2 above" +
      "\nIf some player gets all the cards from the table at the same time, he or she gets a so called sweep which is written down. " +
      "There are a couple of cards that are more valuable in the hand than in the table:" +
      "\n     - Aces: 14 in hand, 1 on table" +
      "\n     - Diamond-10: 16 in hand, 10 on table" +
      "\n     - Spade-2: 15 in hand, 2 on table" +
      "\nNote that for these cards, the cards cannot capture other cards using their value on deck. E.g. 1 Ace cannot capture another Ace, " +
      "Diamond-10 cannot capture 8 and 2. " +
      "The player must also take a card from the deck so that he or she always has 4 cards.",
      "3. Scoring:",
      "When every player runs out of cards, the last player to take cards from the table gets the rest of the cards from the table. " +
      "After this the points are calculated and added to the existing scores. The following things grant points:" +
      "\n     - Every sweep grants 1 point." +
      "\n     - Every Ace grants 1 point." +
      "\n     - The player with most cards gets 1 point." +
      "\n     - The player with most spades gets 2 points." +
      "\n     - The player with Diamonds-10 gets 2 points." +
      "\n     - The player with Spades-2 gets 1 point." +
      "\nOne must collect points which are calculated after every round. The game continues until someone reaches 16 points.")),
    "computer" -> ("Computer Players", "About Computer Players", "User can play against computer players.\nBe careful though, it's not gonna be easy!",
    Vector(
      "In the game, the user can choose to play against 0 to N computer players, " +
      "as long as the number of players does not exceed the limitation of the game (currently 12). There are no separate modes for human vs human players " +
      "and human vs computer players, meaning that user can enjoy playing against fellow human players and computer players simultaneously.",
      "Computer players are equipped with several strategies. " +
      "Along with these strategies, there are also many different types of computer players whose preferences and known strategies vary a lot!",
      "User cannot specifically decide the type of computer player (where's the fun in that?!), but user can somewhat decide their 'smartness'. " +
      "User can decide the 'mode' of each computer player. There are currently 4 modes, Random, Easy, Intermediate, and Hard. " +
      "Although as card game, the best strategies are up to debate, Computer player in higher modes generally 'knows' more or prefer more effective strategies.",
      "Have fun playing the game!")),
    "gui" -> ("User Interface", "About User Interface", "Some notes to help user interact with the App smoothly.",
    Vector(
      "For the most parts, the current User Interface is very simple to use. This section will give some notes for the user about its GUI functionalities." +
      "The User Interface currently has 4 main scenes. User can freely interact with buttons and easily understand their functionalities." +
      "The most important scene is when the game and current round has started. In this scene, the GUI shows the view of the human player in turn, " +
      "including the table, the player's hand, name of the current turn, and the game log.",
      "The user can easily choose a card by clicking on the image of it, double click to cancel the choice, and use buttons to make moves." +
      "After the turn of that player ends, their hand, table, turn will be updated. The game will also announce the next player to be in the view, " +
      "so that the current player can know and switch to the next player in view, before showing the view of that next player.",
      "During the whole App, user can also interact with dialogs which serves different purposes when prompted. E.g. Some announces the invalid moves, " +
      "incorrect user inputs, some announces errors in file-reading / saving...",
      "Overall, the game is quite easy to use. Have fun playing the game!"))
  )


  private val sceneHandler     = new SceneHandler
  private val gameHandler      = new GameHandler
  private val exceptionHandler = new ExceptionHandler

  stage = new JFXApp.PrimaryStage {
    title = "Casino"
    width = 800
    height = 600
  }
  stage.setOnCloseRequest(e => quitGame("interrupted"))
  sceneHandler.menuScene()

  def newGame (): Unit = {
    stage.close()
    gameHandler.newGame()
    sceneHandler.addPlayerScene()
  }
  def addPlayer (name: String, typeName: String, mode: String = "") : Unit = {
    try {
      gameHandler.addPlayer(name,typeName,mode)
      sceneHandler.updatePlayer(gameHandler.givePlayers)
    } catch {
      case e: Exception => exceptionHandler.react(e)
    }
  }
  def removePlayer (player: Player)       : Unit = {
    gameHandler.removePlayer(player)
    sceneHandler.updatePlayer(gameHandler.givePlayers)
  }

  def loadGame (loadMode: String = "main"): Unit = {
    try {
      gameHandler.loadGame(loadMode)
      loadRound()
    } catch {
      case e: Exception => exceptionHandler.react(e)
    }
  }
  def restartGame () : Unit = {
    stage.close()
    gameHandler.restartGame()
    sceneHandler.addPlayerScene()
    sceneHandler.updatePlayer(gameHandler.givePlayers)
  }

  def newRound (): Unit = {
    try {
      gameHandler.newRound()
      loadRound()
    } catch {
      case e: Exception => exceptionHandler.react(e)
    }
  }
  private def loadRound () : Unit = {
    stage.close()
    sceneHandler.updateCurrentTurn(gameHandler.giveTurnName)
    sceneHandler.playerViewScene  (gameHandler.giveTable,gameHandler.giveViewName,gameHandler.giveHand,
                                   gameHandler.giveComments,gameHandler.notiMode)
  }

  def showHints ()                          : Unit = sceneHandler.updateCardsChosen(gameHandler.showHints())
  def showInstruction (instruction: String) : Unit = sceneHandler.instructionNoti(instructions(instruction)._1,instructions(instruction)._2,
                                                                                  instructions(instruction)._3, instructions(instruction)._4)

  def chooseCard (card: Card)                  : Unit = sceneHandler.updateCardsChosen(gameHandler.chooseCard(card))
  def makeMove (moveType: MoveType)            : Unit = {
    try {
      sceneHandler.updateCardsChosen(Vector[Card]())
      gameHandler.makeMove(moveType)
    } catch {
      case e: Exception => exceptionHandler.react(e)
    }
  }
  def responseMove (updatedHand: Vector[Card]) : Unit = {
    sceneHandler.updateHand        (updatedHand)
    sceneHandler.updateTable       (gameHandler.giveTable)
    sceneHandler.updateCurrentTurn (gameHandler.giveTurnName)
    sceneHandler.playerViewScene   (gameHandler.giveTable,gameHandler.giveViewName,gameHandler.giveHand,
                                                          gameHandler.giveComments,gameHandler.notiMode)
  }

  def completeRound () : Unit = {
    stage.close()
    sceneHandler.scoreScene(gameHandler.givePlayers,gameHandler.giveWinners)
  }

  def quitGame (saveMode: String) : Unit = {
    try {
      gameHandler.saveGame(saveMode)
      stage.close()
      stopApp()
    } catch {
      case e: Exception => exceptionHandler.react(e)
    }
  }
}
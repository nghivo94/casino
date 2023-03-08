package GUI

import GUI.SceneElements.{CardLabel, PlayerHandPane, PlayersPane, ScorePane, SummaryPane, TablePane}
import GameLogic.Card.Card
import GameLogic.Move.{Capture, Play}
import GameLogic.Player.Player

import scala.collection.mutable.Map
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, ButtonType, ChoiceBox, Label, ScrollPane, TextField}
import scalafx.scene.image.Image
import scalafx.scene.layout.{Background, _}
import scalafx.scene.paint.Color
import scalafx.scene.Scene
import scalafx.geometry.Pos.Center
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.effect.SepiaTone
import scalafx.scene.text.{Font, FontPosture, FontWeight, Text}

class SceneHandler {
  private val cardLabels = Map[Card,CardLabel]()
  private var currentPlayersPane = new PlayersPane(this)
  private var tablePane          = new TablePane(this)
  private var playerHandPane     = new PlayerHandPane(this)

  private val currentTurn = new Label
  currentTurn.minHeight = 70
  currentTurn.minWidth = 200
  currentTurn.setAlignment(Pos.Center)

  private val instructionButton = new Button("Instruction")
  instructionButton.prefWidth = 30
  instructionButton.text = "?"
  instructionButton.margin = Insets(10)
  instructionButton.setOnAction(e => showInstruction())

  private var summaryPane = new SummaryPane(Vector())

  def menuScene () = {
    val backGround = new BackgroundImage(new Image("file:src/images/CardGameBg.jpg"),BackgroundRepeat.NoRepeat,BackgroundRepeat.NoRepeat,
    BackgroundPosition.Default,new BackgroundSize(BackgroundSize.Auto, BackgroundSize.Auto, true, true, true, true))
    val root = new VBox
    root.background = new Background(Array(backGround))

    val buttonPane = new HBox
    buttonPane.setAlignment(Pos.TopRight)
    buttonPane.getChildren.add(instructionButton)

    val titlePane = new VBox
    titlePane.prefWidth = 800
    titlePane.prefHeight = 600
    titlePane.setAlignment(Pos.Center)

    val title = new Text ("Casino")
    title.setFont(Font.font("Arial", FontWeight.findByWeight(2), FontPosture.Italic, 80))
    title.setFill(Color.White)
    title.margin = Insets(50)
    val newButton = new Button("New Game")
    newButton.prefWidth = 200
    newButton.text = "New Game"
    newButton.setFont(Font.font("Arial Narrow", 20))
    newButton.margin = Insets(15)
    newButton.setOnAction(e => newGame())
    val continueButton = new Button("Continue Game")
    continueButton.prefWidth = 200
    continueButton.text = "Continue Game"
    continueButton.setFont(Font.font("Arial Narrow", 20))
    continueButton.margin = Insets(15)
    continueButton.setOnAction(e => continueGame())
    val quitButton = new Button("Quit Game")
    quitButton.prefWidth = 200
    quitButton.text = "Quit Game"
    quitButton.setFont(Font.font("Arial Narrow", 20))
    quitButton.margin = Insets(15)
    quitButton.setOnAction(e => quitGame())
    titlePane.getChildren.addAll(title,newButton,continueButton,quitButton)
    root.getChildren.addAll(buttonPane,titlePane)
    CasinoApp.stage.setScene(new Scene(root))
  }

  def addPlayerScene () = {
    currentPlayersPane = new PlayersPane(this)
    val root = new VBox

    val addPlayerPane = new BorderPane
    addPlayerPane.setBackground((new Background(Array(new BackgroundFill(Color.web("#2A7A45"), new CornerRadii(0), Insets.Empty)))))
    addPlayerPane.setPrefHeight(120)

    val getNamePane = new HBox
    getNamePane.alignment = Pos.Center
    val nameLabel = new Label("Player's name: ")
    nameLabel.setFont(Font.font("Arial Narrow", 15))
    nameLabel.setTextFill(Color.White)
    nameLabel.margin = new Insets(Insets(20,0,0,0))
    nameLabel.setAlignment(Pos.Center)
    val nameTextField = new TextField()
    nameTextField.margin = new Insets(Insets(20,70,0,20))
    nameTextField.prefWidth = 500
    getNamePane.getChildren.addAll(nameLabel,nameTextField)

    val addButtonsPane = new HBox
    addButtonsPane.alignment = Pos.Center
    val addHumanButton = new Button("Add Human Player")
    addHumanButton.text = "Add Human Player"
    addHumanButton.prefWidth = 150
    addHumanButton.margin = Insets(20)
    addHumanButton.setOnAction (e => {
      addHumanPlayer(nameTextField.getText)
      nameTextField.text = ""
    })
    addHumanButton.setFont(Font.font("Arial Narrow", 15))
    val modeLabel = new Label("Computer mode: ")
    modeLabel.setFont(Font.font("Arial Narrow", 15))
    modeLabel.setTextFill(Color.White)
    val modeChoiceBox = new ChoiceBox[String]
    modeChoiceBox.getItems.addAll("Random", "Easy", "Intermediate", "Hard")
    modeChoiceBox.setValue("Random")
    modeChoiceBox.prefWidth = 100
    modeChoiceBox.margin = Insets(20)
    val addComputerButton = new Button("Add Computer Player")
    addComputerButton.text = "Add Computer Player"
    addComputerButton.prefWidth = 150
    addComputerButton.margin = Insets(20)
    addComputerButton.setOnAction (e => {
      addComputerPlayer(modeChoiceBox.getValue,nameTextField.getText)
      nameTextField.text = ""
    })
    addComputerButton.setFont(Font.font("Arial Narrow", 15))
    addButtonsPane.getChildren.addAll(addHumanButton,addComputerButton,modeLabel,modeChoiceBox,instructionButton)

    addPlayerPane.setCenter(getNamePane)
    addPlayerPane.setBottom(addButtonsPane)

    val currentLabel = new Label("Current Players:")
    currentLabel.setFont(Font.font("Arial",FontWeight.Bold,FontPosture.Regular, 15))
    currentLabel.margin = new Insets(Insets(20,20,20,50))
    val playersPane = new ScrollPane
    playersPane.fitToHeight = true
    playersPane.fitToWidth = true
    playersPane.prefHeight = 500
    playersPane.margin = new Insets(Insets(0,30,0,30))
    playersPane.setContent(currentPlayersPane)
    val startButton = new Button("Start")
    startButton.text = "Start"
    startButton.setFont(Font.font("Arial Narrow", 15))
    startButton.prefWidth = 100
    startButton.margin = new Insets(Insets(20))
    startButton.setOnAction(e => newRound())
    val quitButton = new Button("Quit")
    quitButton.text = "Quit"
    quitButton.setFont(Font.font("Arial Narrow", 15))
    quitButton.prefWidth = 100
    quitButton.margin = new Insets(Insets(20))
    quitButton.setOnAction(e => quitGame())
    val startPane = new HBox
    startPane.prefWidth = 2000
    startPane.alignment = Pos.Center
    startPane.getChildren.addAll(startButton, quitButton)
    root.getChildren.addAll(addPlayerPane,currentLabel,playersPane, startPane)
    CasinoApp.stage.setScene(new Scene(root))
    CasinoApp.stage.show()
  }

  def playerViewScene (table: Vector[Card], nameView: String, hand: Vector[Card], commments: Vector[String], notiMode: String) : Unit = {
    if (notiMode == "next") {
      val nextViewNoti = new Alert(AlertType.Information)
      nextViewNoti.title = "Next Player View"
      nextViewNoti.contentText = "User has finished his/her turn. Move to next player."
      nextViewNoti.showAndWait()
    }
    CasinoApp.stage.close()
    cardLabels.empty
    tablePane      = new TablePane(this)
    playerHandPane = new PlayerHandPane(this)
    summaryPane    = new SummaryPane(commments)

    val root = new VBox

    val utilityPane = new HBox
    utilityPane.setAlignment(Pos.TopRight)
    utilityPane.setBackground(new Background(Array(new BackgroundFill(Color.web("#2A7A45"), new CornerRadii(0), Insets.Empty))))
    val hintButton = new Button("Hints")
    hintButton.prefWidth = 80
    hintButton.text = "Hints"
    hintButton.margin = Insets(10)
    hintButton.setOnAction(e => showHints())
    val restartButton = new Button("Restart")
    restartButton.prefWidth = 80
    restartButton.text = "Restart"
    restartButton.margin = Insets(10)
    restartButton.setOnAction(e => restartGame())
    val quitButton = new Button("Quit")
    quitButton.prefWidth = 80
    quitButton.text = "Quit"
    quitButton.margin = Insets(10)
    quitButton.setOnAction(e => quitGame())
    utilityPane.getChildren.addAll(hintButton, restartButton, quitButton, instructionButton)

    table.foreach( card => {
      val cardLabel = new CardLabel(card,this)
      cardLabels.update(card, cardLabel)
      tablePane.getChildren.add(cardLabel)
    } )

    val infoPane = new VBox
    infoPane.minWidth = 200

    val roundSummaryPane = new ScrollPane
    roundSummaryPane.fitToWidth = true
    roundSummaryPane.fitToHeight = true
    roundSummaryPane.setContent(summaryPane)
    roundSummaryPane.prefHeight = 400
    roundSummaryPane.setVvalue(1.0)

    infoPane.getChildren.addAll(currentTurn, roundSummaryPane)

    hand.foreach( card => {
      val cardLabel = new CardLabel(card,this)
      cardLabels.update(card, cardLabel)
      playerHandPane.getChildren.add(cardLabel)
    } )

    val actionsPane = new VBox
    actionsPane.padding = Insets(10)
    actionsPane.setAlignment(Pos.Center)
    actionsPane.minWidth = 200
    val playButton = new Button("Play")
    playButton.prefWidth = 80
    playButton.text = "Play"
    playButton.margin = Insets(10)
    playButton.setOnAction(e => play())
    val captureButton = new Button("Capture")
    captureButton.prefWidth = 80
    captureButton.text = "Capture"
    captureButton.margin = Insets(10)
    captureButton.setOnAction(e => capture())
    actionsPane.getChildren.addAll(playButton, captureButton)

    val playerPane = new HBox
    playerPane.minHeight = 250
    playerPane.getChildren.addAll(infoPane, playerHandPane, actionsPane)
    root.getChildren.addAll(utilityPane,tablePane,playerPane)
    CasinoApp.stage.setScene(new Scene(root))
    if (!(notiMode == "single")) {
      val playerSceneNoti = new Alert(AlertType.Information)
      playerSceneNoti.title = "Current User View"
      playerSceneNoti.contentText = s"User is viewing from player ${nameView}'s view."
      playerSceneNoti.showAndWait()
    }
    CasinoApp.stage.show()
  }

  def scoreScene (players: Vector[Player], winners: Vector[Player]) = {
    val root = new VBox
    val instructionPane = new HBox
    instructionPane.setAlignment(Pos.TopRight)
    instructionPane.getChildren.add(instructionButton)

    val mainPane = new VBox
    mainPane.setAlignment(Pos.Center)

    val title = new Text ("Score")
    title.setFont(Font.font("Arial", FontWeight.findByWeight(2), FontPosture.Italic, 80))
    title.margin = new Insets(Insets(10,0,30,0))

    val playerScorePane = new VBox
    players.foreach( player => playerScorePane.getChildren.add(new ScorePane(player)) )
    playerScorePane.setBackground(new Background(Array(new BackgroundFill(Color.web("#DCE796"), new CornerRadii(0), Insets.Empty))))
    val playersPane = new ScrollPane
    playersPane.fitToHeight = true
    playersPane.fitToWidth = true
    playersPane.prefHeight = 500
    playersPane.margin = new Insets(Insets(0,30,0,30))
    playersPane.setContent(playerScorePane)

    val newButton = new Button
    if (winners.nonEmpty) {
      newButton.setText("New game")
      newButton.setOnAction(e => newGame())
    }
    else {
      newButton.setText("New round")
      newButton.setOnAction(e => newRound())
    }
    newButton.margin = Insets(20)
    newButton.prefWidth = 100
    val quitButton = new Button("Quit")
    quitButton.prefWidth = 100
    quitButton.text = "Quit"
    quitButton.margin = Insets(10)
    quitButton.setOnAction(e => quitGame())

    val buttonPane = new HBox
    buttonPane.prefWidth = 2000
    buttonPane.alignment = Pos.Center
    buttonPane.getChildren.addAll(newButton, quitButton)

    mainPane.getChildren.addAll(title,playersPane,buttonPane)
    root.getChildren.addAll(instructionPane,mainPane)
    CasinoApp.stage.setScene(new Scene(root))
    CasinoApp.stage.show()
    if (winners.nonEmpty) {
      val winnerAlert = new Alert(AlertType.Information)
      winnerAlert.setTitle("WINNERS")
      winnerAlert.setHeaderText("The winner (s):")
      var winnersList = "     "
      winners.foreach( winner => winnersList += winner.name ++ "\n     " )
      winnerAlert.setContentText(winnersList)
      winnerAlert.show()
    }
  }

  def newGame ()      = CasinoApp.newGame()
  def continueGame () = CasinoApp.loadGame()

  def addHumanPlayer (name: String)                  = CasinoApp.addPlayer(name,"human")
  def addComputerPlayer (mode: String, name: String) = CasinoApp.addPlayer(name,"computer", mode)
  def removePlayer (player: Player)                  = CasinoApp.removePlayer(player)
  def newRound ()  = CasinoApp.newRound()

  def showHints () = CasinoApp.showHints()
  def restartGame () = {
    val restartDialog = new Alert(AlertType.Confirmation)
    restartDialog.title = "Restart"
    restartDialog.headerText = "Restart"
    restartDialog.contentText = "Do you wish to:" +
      "\n   - Start a new Game" +
      "\n   - Restart the Game: Keep the current players" +
      "\n   - Restart the Round: Keep progress from last rounds (if any)"
    restartDialog.getButtonTypes.clear()
    restartDialog.getButtonTypes.addAll(new ButtonType ("New Game"), new ButtonType ("Restart Game"), new ButtonType("Restart Round"), ButtonType.Cancel)
    val option = restartDialog.showAndWait()
    if (option.get != ButtonType.Cancel) {
      option.get.text match {
        case "New Game"      => CasinoApp.newGame()
        case "Restart Game"  => CasinoApp.restartGame()
        case "Restart Round" => CasinoApp.newRound()
      }
    }
  }
  def quitGame () = {
    val quitGameDialog = new Alert(AlertType.Confirmation)
    quitGameDialog.title = "Quit"
    quitGameDialog.headerText = "Quit"
    quitGameDialog.contentText = "Do you want to save the current progress?" +
      "\nNote that if the current game has ended or has not started," +
      "\nnothing will be saved."
    quitGameDialog.getButtonTypes.clear()
    quitGameDialog.getButtonTypes.addAll(new ButtonType("Save"), new ButtonType("Don't save"), new ButtonType("Cancel"))
    (quitGameDialog.showAndWait()).get.text match {
      case "Save"        => CasinoApp.quitGame("save")
      case "Don't save"  => CasinoApp.quitGame("nosave")
      case "Cancel"      =>
    }
  }
  def showInstruction () = {
    val instructionDialog = new Alert(AlertType.Information)
    instructionDialog.title = "Instruction"
    instructionDialog.headerText = "Instruction"
    instructionDialog.contentText = "User may access to the following information:"
    instructionDialog.getButtonTypes.clear()
    instructionDialog.getButtonTypes.addAll(new ButtonType("Rules"), new ButtonType("Computer Players"), new ButtonType("User Interface"), new ButtonType("Cancel"))
    (instructionDialog.showAndWait()).get.text match {
      case "Rules"            => CasinoApp.showInstruction("rules")
      case "Computer Players" => CasinoApp.showInstruction("computer")
      case "User Interface"   => CasinoApp.showInstruction("gui")
      case "Cancel"           =>
    }
  }

  def chooseCard (card: Card) = CasinoApp.chooseCard(card)
  def play ()                 = CasinoApp.makeMove(Play)
  def capture ()              = CasinoApp.makeMove(Capture)

  def updatePlayer (players: Vector[Player]) = currentPlayersPane.updatePlayers(players)
  def updateCurrentTurn (name: String) = currentTurn.setText(s"Player ${name}'s turn")
  def updateCurrentComments (comments: Vector[String]) = summaryPane = new SummaryPane(comments)
  def updateCardsChosen (cardsChosen: Vector[Card]) = {
    cardLabels.values.foreach( _.setEffect(null) )
    cardsChosen.foreach( card => {
      val sepiaTone = new SepiaTone()
      sepiaTone.setLevel(1)
      cardLabels(card).setEffect(sepiaTone)
    } )
  }
  def updateTable (table: Vector[Card]) = tablePane.updateTable(table)
  def updateHand  (hand: Vector[Card])  = playerHandPane.updateHand(hand)

  def instructionNoti (title: String, header: String, context: String, instruction: Vector[String]) = {
    val instructionPane = new VBox
    instruction.foreach( line => {
      val textNode = new Text
      textNode.setText(line)
      textNode.setFont(Font.font("Times"))
      textNode.wrappingWidth = 320
      textNode.margin = new Insets(Insets(5,5,10,0))
      instructionPane.getChildren.add(textNode)
    } )
    val instructionScrollPane = new ScrollPane
    instructionScrollPane.setContent(instructionPane)
    instructionScrollPane.prefViewportHeight = 200
    val instructionDialog = new Alert(AlertType.Information)
    instructionDialog.title = title
    instructionDialog.headerText = header
    instructionDialog.contentText = context
    instructionDialog.dialogPane().setExpandableContent(instructionScrollPane)
    instructionDialog.showAndWait()
  }
}
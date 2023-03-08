import GameLogic.Card.{Card, CardDeck, Table}
import GameLogic.Move.{Capture, Play}
import GameLogic.Player._
import GameLogic.Round
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RoundTests extends AnyFlatSpec with Matchers {
  val cardDeck = new CardDeck
  cardDeck.loadDeck(Some(Vector(Card("h5"),Card("d0"))))
  val table = new Table
  table.loadTable(Some(Vector(Card("c3"),Card("ca"),Card("s4"))))
  val playerNames  = Vector(Some("Jack"),Some("Vienna"),Some("Amy"),Some("Roger"))
  val playerTypes  = Vector(Some("human"))
  val playerOrders = Vector(Some("3"),Some("1"),Some("0"),Some("4"))
  val playerScores = Vector(Some("3"),Some("1"),Some("0"),Some("4"))
  val playerSweeps = Vector(Some("3"),Some("1"),Some("0"),Some("4"))

  val playerHands  = Vector(Some(Vector(Card("dk"))),
                            Some(Vector(Card("h3"))),
                            Some(Vector(Card("c5"))),
                            Some(Vector(Card("d6"))))

  val playerPiles  = Vector(Some(Vector()),
                            Some(Vector(Card("s2"), Card("d3"), Card("da"), Card("sa"))),
                            Some(Vector(Card("s0"), Card("d9"), Card("ha"), Card("c6"))),
                            Some(Vector(Card("s7"), Card("d7"), Card("h0"), Card("c4"))))
  val players = Vector(
    Player(playerNames(0),playerTypes(0),playerOrders(0),playerScores(0),playerSweeps(0),playerHands(0),playerPiles(0),Some(Vector())),
    Player(playerNames(1),playerTypes(0),playerOrders(1),playerScores(1),playerSweeps(1),playerHands(1),playerPiles(1),Some(Vector())),
    Player(playerNames(2),playerTypes(0),playerOrders(2),playerScores(2),playerSweeps(2),playerHands(2),playerPiles(2),Some(Vector())),
    Player(playerNames(3),playerTypes(0),playerOrders(3),playerScores(3),playerSweeps(3),playerHands(3),playerPiles(3),Some(Vector())),
  )
  val round1 = new Round(cardDeck,table,players)
  val round2 = new Round(cardDeck,table,players)

  "Round" should "response to moves appropriately." in {
    players(1).chooseCard(Card("h3"))
    players(1).chooseCard(Card("c3"))
    round1.responseMove(players(1).makeMove(Some(Capture)))
    assert(table.giveTable === Vector(Card("ca"),Card("s4")))
    assert(round1.isComplete === false)
    assert(players(1).giveHand === Vector(Card("h5")))
    assert(players(1).givePile === Vector(Card("s2"), Card("d3"), Card("da"), Card("sa"), Card("h3"), Card("c3")))
    assert(round1.giveCurrentTurn === players(2))
    assert(round1.giveCurrentView === players(2))

    players(2).chooseCard(Card("c5"))
    players(2).chooseCard(Card("ca"))
    players(2).chooseCard(Card("s4"))
    round1.responseMove(players(2).makeMove(Some(Capture)))
    assert(table.hasSweep === true)
    assert(round1.isComplete === false)
    assert(players(2).giveHand === Vector(Card("d0")))
    assert(players(2).givePile === Vector(Card("s0"), Card("d9"), Card("ha"), Card("c6"), Card("c5"), Card("ca"), Card("s4")))
    assert(cardDeck.giveDeck === Vector())
    assert(round1.giveCurrentTurn === players(3))
    assert(round1.giveCurrentView === players(3))

    players(3).chooseCard(Card("d6"))
    round1.responseMove(players(3).makeMove(Some(Play)))
    assert(table.giveTable === Vector(Card("d6")))
    assert(round1.isComplete === false)
    assert(players(3).giveHand === Vector())
    assert(players(3).givePile === Vector(Card("s7"), Card("d7"), Card("h0"), Card("c4")))
    assert(round1.giveCurrentTurn === players(0))
    assert(round1.giveCurrentView === players(0))

    players(0).chooseCard(Card("dk"))
    round1.responseMove(players(0).makeMove(Some(Play)))
    players(1).chooseCard(Card("h5"))
    round1.responseMove(players(1).makeMove(Some(Play)))
    players(2).chooseCard(Card("d0"))
    round1.responseMove(players(2).makeMove(Some(Play)))
    assert(table.giveTable === Vector(Card("d6"),Card("dk"),Card("h5"),Card("d0")))
    assert(round1.isComplete === true)
  }

  it should "end a round appropriately." in {
    round1.endRound()
    assert(players(0).giveScore === 6)
    assert(players(1).giveScore === 7)
    assert(players(2).giveScore === 8)
    assert(players(3).giveScore === 8)
    assert(table.giveTable === Vector())
    players.foreach( player => assert(player.giveHand === Vector()))
    players.foreach( player => assert(player.givePile === Vector()))
  }

  it should "start a new round appropriately" in {
    round2.startRound()
    players.foreach( player => assert(player.giveHand.size === 4) )
    assert(cardDeck.giveDeck.size === 32)
    assert(table.giveTable.size === 4)
    assert(round2.giveCurrentTurn === players(1))
    assert(round2.giveCurrentView === players(1))
    assert(round2.isComplete === false)
  }
}

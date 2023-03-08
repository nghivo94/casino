import Exceptions.{IllegalPlayerInfo, MissingPlayerInfo, NoNameChosen}
import GameLogic.Card.Card
import GameLogic.Player.Player
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlayerTests extends AnyFlatSpec with Matchers {
  val names = Vector(None, Some("Vienna"))
  val playerTypes = Vector(None, Some("human"), Some("special computer"), Some("strange"))
  val orders = Vector (Some("0"))
  val scores = Vector (None, Some("3"), Some("0"), Some("-1"), Some("strange"))
  val sweeps = Vector (Some("0"))
  val cards = Vector(None, Some(Vector(Card("s2"), Card("d2"))), Some(Vector(Card("c5"))), Some(Vector(Card("s2"), Card("d3"), Card("ca"), Card("sa"))),
    Some(Vector(Card("s0"), Card("d9"), Card("ca"), Card("c6"))), Some(Vector(Card("s7"), Card("d7"), Card("h0"), Card("c4"), Card("sa"))),
    Some(Vector())
  )

  "Player" should "be created with correct values with appropriate parameters in 1st apply function (from loaded file)." in {
    val validPlayer1 = Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))
    assert(validPlayer1.name === "Vienna")
    assert(validPlayer1.isComputer === false)
    assert(validPlayer1.giveScore === 3)
    assert(validPlayer1.giveHand === cards(1).get)
    assert(validPlayer1.givePile === cards(2).get)
    val validPlayer2 = Player(names(1),playerTypes(2),orders(0),scores(2),sweeps(0),cards(1),cards(2),cards(3))
    assert(validPlayer2.name === "Vienna")
    assert(validPlayer2.isComputer === true)
    assert(validPlayer2.giveScore === 0)
  }

  it should "throw exceptions if player's information is missing with 1st apply function (from loaded file)." in {
    assert(intercept[MissingPlayerInfo]{Player(names(0),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))}.text === "Missing player's name.")
    assert(intercept[MissingPlayerInfo]{Player(names(1),playerTypes(0),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))}.text === "Missing player's type.")
    assert(intercept[MissingPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(0),sweeps(0),cards(1),cards(2),cards(3))}.text === "Missing player's score.")
    assert(intercept[MissingPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(0),cards(2),cards(3))}.text === "Missing player's cards on hand.")
    assert(intercept[MissingPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(0),cards(3))}.text === "Missing player's cards in pile.")
    assert(intercept[MissingPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(0))}.text === "Missing player's cards seen.")
  }

  it should "throw exceptions if player's information given is invalid with 1st apply function (from loaded file)." in {
    assert(intercept[IllegalPlayerInfo]{Player(names(1),playerTypes(3),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))}.text === "Invalid player's type: strange.")
    assert(intercept[IllegalPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(3),sweeps(0),cards(1),cards(2),cards(3))}.text === "Invalid player's score: -1.")
    assert(intercept[IllegalPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(4),sweeps(0),cards(1),cards(2),cards(3))}.text === "Invalid player's score: strange.")
    assert(intercept[IllegalPlayerInfo]{Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(5),cards(2),cards(3))}.text === "Invalid player's hand. Cards on player's hand exceed 4.")
  }

  it should "be created with correct values with 2nd apply function (only for Computer players)." in {
    val validPlayer = Player("computer", 2, "random")
    assert(validPlayer.isComputer === true)
    assert(validPlayer.giveScore === 0)
    assert(validPlayer.giveHand === Vector())
    assert(validPlayer.givePile === Vector())
  }

  it should "throw exceptions when a human player is created without name parameter with 2nd apply function." in {
    assertThrows[NoNameChosen]{Player("human", 3, "random")}
  }

  it should "be created appropriately with 3rd apply function." in {
    val validPlayer1 = Player(names(1).get, 0,"human")
    assert(validPlayer1.name === "Vienna")
    assert(validPlayer1.isComputer === false)
    assert(validPlayer1.giveScore === 0)
    assert(validPlayer1.giveHand === Vector())
    assert(validPlayer1.givePile === Vector())

    val validPlayer2 = Player(names(1).get,1,"computer")
    assert(validPlayer2.name === "Vienna")
    assert(validPlayer2.isComputer === true)
    assert(validPlayer2.giveScore === 0)
    assert(validPlayer2.giveHand === Vector())
    assert(validPlayer2.givePile === Vector())
  }

  it should "add / remove cards to cards on hand, in pile, seen appropriately." in {
    val player = Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))
    assume(player.giveHand === cards(1).get)
    assume(player.givePile === cards(2).get)
    player.addHand(Vector(Card("s0")))
    player.addPile(Vector(Card("s7")))
    player.addSeen(Vector(Card("d0")))
    assert(player.giveHand === cards(1).get ++ Vector(Card("s0")))
    assert(player.givePile === cards(2).get ++ Vector(Card("s7")))
    player.removeHand(Card("s0"))
    assert(player.giveHand === cards(1).get)
    assert(player.givePile === cards(2).get ++ Vector(Card("s7")))
  }

  it should "check if there is cards on hand." in {
    val player1 = Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))
    assert(player1.hasCards === true)

    val player2 = Player(names(1),playerTypes(2),orders(0),scores(1),sweeps(0),cards(6),cards(2),cards(3))
    assert(player2.hasCards === false)
  }

  it should "give and add score appropriately." in {
    val player = Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))
    assume(player.giveScore === 3)
    player.addScore()
    assert(player.giveScore === 4)
  }

  it should "choose cards and give chosen cards appropriately." in {
    val player = Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))
    assert(player.giveChosenCards === Vector())
    player.chooseCard(Card("d7"))
    assert(player.giveChosenCards === Vector(Card("d7")))
    player.chooseCard(Card("s0"))
    assert(player.giveChosenCards === Vector(Card("d7"), Card("s0")))
    player.chooseCard(Card("s2"))
    assert(player.giveChosenCards === Vector(Card("s2"),Card("d7"), Card("s0")))
    player.chooseCard(Card("d2"))
    assert(player.giveChosenCards === Vector(Card("d2"), Card("d7"), Card("s0")))
  }

  it should "reset round information appropriately." in {
    val player = Player(names(1),playerTypes(1),orders(0),scores(1),sweeps(0),cards(1),cards(2),cards(3))
    assume(player.name === "Vienna")
    assume(player.isComputer === false)
    assume(player.giveScore === 3)
    assume(player.giveHand === cards(1).get)
    assume(player.givePile === cards(2).get)

    player.resetRoundInfo()
    assert(player.name === "Vienna")
    assert(player.isComputer === false)
    assert(player.giveScore === 3)
    assert(player.giveHand === Vector())
    assert(player.givePile === Vector())
  }
}

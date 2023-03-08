import Exceptions.{MissingCardDeckInfo, NotEnoughCardsInDeck}
import GameLogic.Card.{Card, CardDeck}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardDeckTests extends AnyFlatSpec with Matchers {
  val cardDeck = new CardDeck
  val testDecks = Vector(Some(Vector(Card("h3"),Card("ca"),Card("c5"))), Some(Vector()),
    Some(Vector(Card("s4"),Card("s4"),Card("d7"))), None)

  "CardDeck" should "generates a full deck with 52 distinct cards." in {
    cardDeck.newDeck()
    assert(cardDeck.giveDeck.size === 52)
    assert(cardDeck.giveDeck.distinct.size === 52)
  }

  it should "load an existing deck appropriately." in {
    cardDeck.loadDeck(testDecks(0))
    assert(cardDeck.giveDeck === testDecks(0).get)
    cardDeck.loadDeck(testDecks(1))
    assert(cardDeck.giveDeck === Vector())
    cardDeck.loadDeck(testDecks(2))
    assert(cardDeck.giveDeck === Vector(Card("s4"),Card("d7")))
  }

  it should "detect if the information about the card deck is missing." in {
    assertThrows[MissingCardDeckInfo] {cardDeck.loadDeck(testDecks(3))}
  }

  it should "draw cards appropriately." in {
    cardDeck.loadDeck(testDecks(0))
    assert(cardDeck.drawACard() === Vector(Card("h3")))
    assert(cardDeck.giveDeck === Vector(Card("ca"),Card("c5")))
    assert(cardDeck.drawTwoCards() === Vector(Card("ca"),Card("c5")))
    assert(cardDeck.giveDeck === Vector())
    assert(cardDeck.drawACard() === Vector())
  }

  it should "detect exceptions appropriately" in {
    cardDeck.loadDeck(testDecks(1))
    assert(cardDeck.giveDeck === Vector())
    assert(intercept[NotEnoughCardsInDeck]{cardDeck.drawTwoCards()}.text === "Not enough cards to deal.")
    assert(cardDeck.giveDeck === Vector())
  }
}

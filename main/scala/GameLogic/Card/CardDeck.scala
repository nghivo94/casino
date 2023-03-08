package GameLogic.Card

import Exceptions.{MissingCardDeckInfo, NotEnoughCardsInDeck}

import scala.util.Random

class CardDeck {
  //Storing and accessing the current cards in deck
  private var cardsInDeck : Vector[Card] = Vector()
  def giveDeck            : Vector[Card] = cardsInDeck

  //Accessing the whole deck from Card Object and shuffle it, used for every new round.
  def newDeck () : Unit = {
    cardsInDeck = Card.allCards
    cardsInDeck = Random.shuffle(cardsInDeck)
  }

  //Loading the deck from file.
  def loadDeck (opCards: Option[Vector[Card]]) : Unit = cardsInDeck = opCards match {
    case Some(cards) => cards.distinct
    case None => throw MissingCardDeckInfo("Missing information about cards in deck.")
  }

  //Drawing 2 cards, used for dealing.
  def drawTwoCards () : Vector[Card] = {
    if (cardsInDeck.size < 2) throw NotEnoughCardsInDeck("Not enough cards to deal.")
    val res = cardsInDeck.take(2)
    cardsInDeck = cardsInDeck.tail.tail
    res
  }

  //Drawing a card after each turn.
  def drawACard () : Vector[Card]  = cardsInDeck.headOption match {
    case Some(card) => cardsInDeck = cardsInDeck.tail
                       Vector(card)
    case None => Vector()
  }
}
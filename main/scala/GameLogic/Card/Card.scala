package GameLogic.Card

import Exceptions.UnknownCard

//To utilize the match - case structure, Card class is a case class, whose functions are all effect-free.
case class Card (suit: Suit, nameVal: String, numVal: Int, valueInHand: Int) {

  //This function returns a name used for game log to describe a card. e.g. Ace of Hearts, Diamond - 10, ...
  def giveName : String = nameVal.toIntOption match {
    case Some(0) => suit.suitName + "-10"
    case Some(_) => suit.suitName + "-" + nameVal
    case None => nameVal + " of " + suit.suitName + "s"
  }

  //This function returns the image path of the image for the corresponding card.
  def giveImgPath : String = "file:src/images/" + (nameVal match {
    case "0" => "10"
    case other => other.toLowerCase()
  }) + "_of_" + suit.suitName.toLowerCase() + "s.png"

  //This function returns the format with which the card should be saved.
  def giveSaveDetail : String = suit.suitName(0).toString.toLowerCase + nameVal(0).toString.toLowerCase
}

object Card {
  //Storing all possible choices of suits, values, names, ...
  private val allSuits  = Map(("h", Heart), ("s", Spade), ("c", Club), ("d", Diamond))
  private val allValues = Map(("2", 2), ("3", 3), ("4", 4), ("5", 5), ("6", 6), ("7", 7), ("8", 8), ("9", 9), ("0", 10),
                              ("Jack", 11), ("Queen", 12), ("King", 13), ("Ace", 1))

  //An immutable collection of total cards (52 cards)
  val allCards      = generateAllCards

  //An immutable collection of cards of all values in hand (currently 15 values from 2 to 16)
  val standardCards = Vector(Card("h2"),Card("h3"),Card("h4"),Card("h5"),Card("h6"),Card("h7"),Card("h8"),Card("h9"),Card("h0"),
                             Card("hj"),Card("hq"),Card("hk"),Card("ha"),Card("s2"),Card("d0"))

  //Factory method for creation of a card from 2-letter strings.
  //This method also "check" if card information is valid.
  def apply (cardInfo: String) : Card = {
    val info = cardInfo match {
      case unknown if unknown.length != 2 => throw UnknownCard("Missing card info.")
      case card => card
    }
    val suit = info(0).toString match {
      case unknown if (!allSuits.keys.toVector.contains(unknown)) => throw UnknownCard(s"Unknown card suit: ${unknown}.")
      case known => allSuits(known)
    }
    val nameVal = info(1).toString match {
      case number if (number.toIntOption.nonEmpty && number.toInt != 1) => number
      case "j" => "Jack"
      case "q" => "Queen"
      case "k" => "King"
      case "a" => "Ace"
      case unknown => throw UnknownCard(s"Unknown card value: ${unknown}.")
    }
    val numVal = allValues(nameVal)
    val valueInHand = findValueInHand(suit,numVal)
    new Card(suit,nameVal,numVal,valueInHand)
  }

  //Private def to help generating whole card deck, only used once for generating allCards.
  private def generateAllCards : Vector[Card] = {
    var result : Vector[Card] = Vector()
    val suits  = allSuits.values.toVector
    val values = allValues.toVector
    for (i <- suits.indices) {
      for (j <- values.indices) result = result ++ Vector(new Card(suits(i),values(j)._1,values(j)._2,findValueInHand(suits(i),values(j)._2)))
    }
    result
  }

  //As there are special cards with different values in hand, this private def helps to find these values.
  private def findValueInHand (suit: Suit, numVal: Int) : Int = (suit, numVal) match {
    case (_,1)        => 14
    case (Diamond,10) => 16
    case (Spade,2)    => 15
    case (_,other)    => other
  }
}
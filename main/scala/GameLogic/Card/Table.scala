package GameLogic.Card

import Exceptions.MissingTableInfo

class Table {
  //Storing and accessing the current cards on table
  private var cardsOnTable : Vector[Card] = Vector()
  def giveTable            : Vector[Card] = cardsOnTable

  //Loading the table from file.
  def loadTable  (opCards: Option[Vector[Card]]) : Unit = cardsOnTable = opCards match {
    case Some(cards) => cards.distinct
    case None => throw MissingTableInfo("Missing information about cards on table.")
  }

  //Adding and Removing Cards.
  def addCards   (cards: Vector[Card]) : Unit = cardsOnTable = (cardsOnTable ++ cards).distinct
  def removeCards(cards: Vector[Card]) : Unit = cards.foreach( card => cardsOnTable = cardsOnTable.filter( _ != card ) )

  //Reseting the table, used when restarting game/round.
  def resetTable () : Unit    = cardsOnTable = Vector()

  //Used to check if there has been a sweep.
  def hasSweep      : Boolean = cardsOnTable.isEmpty
}

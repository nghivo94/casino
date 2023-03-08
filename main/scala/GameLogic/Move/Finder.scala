package GameLogic.Move

import GameLogic.Card.Card
import scala.collection.mutable.Set

class Finder (table: Vector[Card], hand: Vector[Card]) {

  //The task of finding all moves are divided into different phases.

  //For each card in hand (that may be played), there is a componentCaptures Map and an allCaptures Set.
  //The componentCaptures are the sets of cards that may be captured by the played cards.
  //The sets of cards captured in the component captures are the "smallest" sets of cards that can be captured by the played card,
                              //whose values sum up to exactly the value in hand of the played card.
  //The allCaptures are the set of sets of Cards that may be captured by the played card.
  private val componentCaptures : Set[Vector[Card]] = Set()
  private val allCaptures       : Set[Vector[Card]] = Set()


  //This is a recursive function used to add to the componentCaptures the sets of cards,
                                                  // whose values sum up to the exact value in hand of the played card.
  private def sumCaptureRec (cards: Vector[Card], len: Int, current: Vector[Card], target: Int): Unit = {
    if (target == 0) if (target == 0) componentCaptures.add(current) //If target sum is met, add the current combination
    if (len == 0 || target < 0) return //Operation stops when last card is reached / target sum lower than 0.
    // Consider 2 scenarios: The current card is included and not included.
    sumCaptureRec(cards,len-1,current,target)
    sumCaptureRec(cards,len-1,current ++ Vector(cards(len-1)),target - cards(len-1).numVal)
  }

  //This is the function that finds all componentCaptures, which includes the cards with the same value as the played cards.
  //This is performed separately to reduce time cost.
  private def findComponentCaptures (table: Vector[Card], target: Card) : Unit = {
    table.filter( _.numVal == target.valueInHand ).foreach( card => componentCaptures.add(Vector(card)) ) //Add cards with same value
    val remainignCards = table.filter( _.numVal != target.valueInHand)
    sumCaptureRec(remainignCards,remainignCards.length,Vector(),target.valueInHand) //Add cards with sum captures.
  }

  //This is a recursive function to "stack" the componentCaptures together.
  private def stackComponentsRec (current: Vector[Card], source: Vector[Vector[Card]]): Unit = {
    if (current.nonEmpty) allCaptures.add(current) //Add the current acceptable group
    if (source.isEmpty) return //If there are no more component, operation stops
    //Traverse the remaining components
    for (i <- source.indices) {
      val currentSource    = source.drop(i)
      val currentComponent = currentSource.head
        //Find the next source by filtering the components with same cards.
      val nextSource       = currentSource.filter( component => !(currentComponent.exists( card => component.contains(card) )) )
      stackComponentsRec(current ++ currentComponent, nextSource)
    }
  }

  //This is the function to combine the previous functions to find all capture moves.
  private def findCaptures (table: Vector[Card], target: Card) : Unit = {
    findComponentCaptures(table,target)
    stackComponentsRec(Vector(),componentCaptures.toVector)
  }

  //This is the function to find all moves, including the play moves.
  def findAllMoves () : Vector[Move] = {
    var allMoves : Set[Move] = Set()
    for (card <- hand) {
      componentCaptures.clear() //Reset the captured cards for every card played.
      allCaptures      .clear()
      findCaptures(table, card)
      allCaptures.foreach( capture => allMoves.add(Move(Capture,card,capture)) )
      allMoves.add(Move(Play, card, Vector()))
    }
    allMoves.toVector //Return all moves
  }
}

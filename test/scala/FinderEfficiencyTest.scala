import GameLogic.Card.{Card, CardDeck}
import GameLogic.Move.{Capture, Move, Play}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.{Map, Set}

class FinderEfficiencyTest extends AnyFlatSpec with Matchers {
  val cardDeck = new CardDeck
  var duration1 = 0
  var duration2 = 0

  for (i <- 0 until 10) {
    cardDeck.newDeck()
    val table = cardDeck.giveDeck.take(20)
    val hand = cardDeck.giveDeck.takeRight(4)
    val startTime1 = System.currentTimeMillis()
    val result1 = (new Finder1(table,hand)).findAllMoves()
    val endTime1 = System.currentTimeMillis()
    duration1 += (endTime1 - startTime1).toInt
    val startTime2 = System.currentTimeMillis()
    val result2 = (new Finder2(table,hand)).findAllMoves()
    val endTime2 = System.currentTimeMillis()
    duration2 += (endTime2 - startTime2).toInt
    assert(result1.length === result2.length)
    println(result2.length)
  }
  println(s"First implementation: ${duration1} millis.")
  println(s"Second implementation: ${duration2} millis.")
}

class Finder1  (table: Vector[Card], hand: Vector[Card]) {
  private val componentCaptures : Map[Card,Set[Vector[Card]]] = Map()
  private val allCaptures       : Set[Vector[Card]]           = Set()
  private def sumCaptureRec (cards: Vector[Card], len: Int, current: Vector[Card], target: Int): Unit = {
    if (target == 0) current.foreach( componentCaptures(_).add(current) )
    if (len == 0 || target < 0) return
    sumCaptureRec(cards,len-1,current,target)
    val nextCurrent = current ++ Vector(cards(len-1))
    sumCaptureRec(cards,len-1,nextCurrent,target - cards(len-1).numVal)
  }
  private def findComponentCaptures (table: Vector[Card], target: Card) : Unit = {
    table.foreach( card => componentCaptures.update(card, Set()) )
    table.filter( _.numVal == target.valueInHand ).foreach( card => componentCaptures(card).add(Vector(card)) )
    val remainignCards = table.filter( _.numVal != target.valueInHand)
    sumCaptureRec(remainignCards,remainignCards.length,Vector(),target.valueInHand)
  }
  private def stackComponentsRec (current: Vector[Card], source: Vector[Vector[Card]]): Unit = {
    if (current.nonEmpty) allCaptures.add(current)
    if (source.isEmpty) return
    for (i <- source.indices) {
      val currentSource      = source.drop(i)
      val currentComponent   = currentSource.head
      val excludedComponents = currentComponent.flatMap( componentCaptures(_) )
      val nextSource         = currentSource.filter( !excludedComponents.contains(_) )
      stackComponentsRec(current ++ currentComponent, nextSource)
    }
  }
  private def findCaptures (table: Vector[Card], target: Card) : Unit = {
    findComponentCaptures(table,target)
    val componentVec = componentCaptures.values match {
      case empty if componentCaptures.isEmpty => Vector()
      case nonEmpty                           => nonEmpty.reduce( _ ++ _ ).toVector
    }
    stackComponentsRec(Vector(),componentVec)
  }
  def findAllMoves () : Vector[Move] = {
    var allMoves : Set[Move] = Set()
    for (card <- hand) {
      componentCaptures.clear()
      allCaptures      .clear()
      findCaptures(table, card)
      allCaptures.foreach( capture => allMoves.add(Move(Capture,card,capture)) )
      allMoves.add(Move(Play, card, Vector()))
    }
    allMoves.toVector
  }
}

class Finder2 (table: Vector[Card], hand: Vector[Card]) {
  private val componentCaptures : Set[Vector[Card]] = Set()
  private val allCaptures       : Set[Vector[Card]] = Set()
  private def sumCaptureRec (cards: Vector[Card], len: Int, current: Vector[Card], target: Int): Unit = {
    if (target == 0) componentCaptures.add(current)
    if (len == 0 || target < 0) return
    sumCaptureRec(cards,len-1,current,target)
    val nextCurrent = current ++ Vector(cards(len-1))
    sumCaptureRec(cards,len-1,nextCurrent,target - cards(len-1).numVal)
  }
  private def findComponentCaptures (table: Vector[Card], target: Card) : Unit = {
    table.filter( _.numVal == target.valueInHand ).foreach( card => componentCaptures.add(Vector(card)) )
    val remainignCards = table.filter( _.numVal != target.valueInHand)
    sumCaptureRec(remainignCards,remainignCards.length,Vector(),target.valueInHand)
  }
  private def stackComponentsRec (current: Vector[Card], source: Vector[Vector[Card]]): Unit = {
    if (current.nonEmpty) allCaptures.add(current)
    if (source.isEmpty) return
    for (i <- source.indices) {
      val currentSource      = source.drop(i)
      val currentComponent   = currentSource.head
      val nextSource         = currentSource.filter( component => !(currentComponent.exists( card => component.contains(card) )) )
      stackComponentsRec(current ++ currentComponent, nextSource)
    }
  }
  private def findCaptures (table: Vector[Card], target: Card) : Unit = {
    findComponentCaptures(table,target)
    stackComponentsRec(Vector(),componentCaptures.toVector)
  }
  def findAllMoves () : Vector[Move] = {
    var allMoves : Set[Move] = Set()
    for (card <- hand) {
      componentCaptures.clear()
      allCaptures      .clear()
      findCaptures(table, card)
      allCaptures.foreach( capture => allMoves.add(Move(Capture,card,capture)) )
      allMoves.add(Move(Play, card, Vector()))
    }
    allMoves.toVector
  }
}
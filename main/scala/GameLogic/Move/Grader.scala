package GameLogic.Move

import GameLogic.Card.{Card, Spade}

import scala.collection.mutable.Map
import scala.util.Random

class Grader (move: Move, preferences: Map[String,Int], table: Vector[Card],
              hand: Vector[Card] = Vector(),seen: Vector[Card] = Vector()) {

  //The grader has several strategies, the name of all are stored in the allStrategies Map and accessed with a key word.
  private val allStrategies : Map[String, () => Unit] = Map(
    "prispecial"  -> priorSpecialCardsStrategy,
    "primore"     -> priorMoreCardsStrategy,
    "pribuild"    -> priorBuildPlayStrategy,
    "avsweep"     -> avoidEasySweepStrategy,
    "prisweep"    -> priorSweepStrategy,
    "prispade"    -> priorMoreSpadesStrategy,
    "avpowerplay" -> avoidPowerPlayStrategy,
    "prilast"     -> priorLastCaptureStrategy,
    "prirandom"   -> priorRandomStrategy,
    "pricapture"  -> priorCaptureStrategy)

  //The points corresponding to each strategies are stored in the allPoints Map, accessed by the same keywords.
  private val allPoints     : Map[String, Int]        = Map()
  allStrategies.keys.foreach( allPoints.update(_,0) )

  //Some strategies are more complicated, some are quite simple.
  private def avoidPowerPlayStrategy () : Unit = if (move.moveType == Play) move.cardPlayed match {
    case Card(_, _, _, 14) => updateAllPoints("avpowerplay", -200)
    case Card(_, _, _, 15) => updateAllPoints("avpowerplay", -200)
    case Card(_, _, _, 16) => updateAllPoints("avpowerplay", -400)
    case other =>
  }

  private def priorCaptureStrategy () : Unit = if (move.moveType == Capture) updateAllPoints("pricapture", 1)
  private def priorRandomStrategy ()  : Unit = updateAllPoints("prirandom", Random.nextInt(11))

  private def priorLastCaptureStrategy ()  : Unit = if (move.moveType == Capture) updateAllPoints("prilast", 50*(4-hand.length))
  private def priorSpecialCardsStrategy () : Unit = if (move.moveType == Capture) (move.cardsCaptured ++ Vector(move.cardPlayed)).foreach {
    case Card(_, _, _, 14) => updateAllPoints("prispecial", 300)
    case Card(_, _, _, 15) => updateAllPoints("prispecial", 300)
    case Card(_, _, _, 16) => updateAllPoints("prispecial", 600)
    case other =>
  }

  private def priorSweepStrategy () : Unit = if (move.cardsCaptured.length == table.length && table.nonEmpty) updateAllPoints("prisweep", 300)

  private def priorMoreCardsStrategy ()  : Unit = if (move.moveType == Capture) updateAllPoints("primore", (move.cardsCaptured.length + 1)*10)
  private def priorMoreSpadesStrategy () : Unit = if (move.moveType == Capture) updateAllPoints("prispade",(move.cardsCaptured ++ Vector(move.cardPlayed)).count(_.suit == Spade)*10)

  ///The priorBuildPlayStrategy grades the Play moves.
  private def priorBuildPlayStrategy () : Unit = if (move.moveType == Play) {
    //It finds the possible moves with the cards on table and the played cards, using the remaining cards in hand
    val possiblePoints = (new Finder(table ++ Vector(move.cardPlayed), hand.filter( _!= move.cardPlayed))).findAllMoves()
      .map( move => (new Grader(move, preferences, table ++ Vector(move.cardPlayed))).halfGrade() ) //and maps them to their points after "half-grade".
    if (possiblePoints.nonEmpty) updateAllPoints("pribuild", possiblePoints.max - 150) //then applies a "penalty" for being only "possible".
  }
  private def halfGrade () : Int = { //This is a helper function for priorBuildPlay, as some strategies may not be needed when considering future moves.
    priorBuildPlayStrategy()                                //e.g. avoidSweep / priorSweep, ...
    priorSpecialCardsStrategy()
    priorMoreCardsStrategy()
    priorMoreSpadesStrategy()
    avoidPowerPlayStrategy()
    allPoints.values.sum
  }

  //The avoidEasySweepStrategy grades all moves to avoid easy sweep for the next player.
                                                     //This excludes the situation that that player has a sweep.
  private def avoidEasySweepStrategy () : Unit = if (!(move.cardsCaptured.length == table.length && table.nonEmpty)) {
    val seenValues : Map[Int,Int] = Map() //Creating a map for seen values.
    Card.standardCards.map( _.valueInHand).foreach( seenValues.update(_, 0) ) //Initiating the Map with all values in hand from Card.standardCards
    seen.foreach( card => seenValues.update(card.valueInHand,seenValues(card.valueInHand)+1) ) //Updating the seen values from seen cards.
    val sweepValues = move.moveType match { //It finds possible values in hand of the cards that may be played to make a sweep
      case Play    => findPossibleCardSweep(table, move.cardsCaptured, Vector(move.cardPlayed))
      case Capture => findPossibleCardSweep(table, move.cardsCaptured, Vector())
    }
    if (sweepValues.nonEmpty) sweepValues.foreach {
    //Using the "seenValues", it determines how possible that other players may have these "sweep values",
    //that is based on how many cards with these values are yet to be seen. And it gives penalty based on this possibility.
      //For the values 15 and 16, there is only 1 card each that has these values.
      case special if special >= 15 => updateAllPoints("avsweep", -(1 - seenValues(special))*50)
      case other                    => updateAllPoints("avsweep", -(4 - seenValues(other))*50)
    }
  }
  //This is a helper function for the avoidEasySweepStrategy.
  private def findPossibleCardSweep (table: Vector[Card], capture: Vector[Card], play: Vector[Card]) : Vector[Int] = {
    var sweepValues : Vector[Int] = Vector()
    val remainingCards = table.filter( !capture.contains(_) ) ++ play //It determines what cards are on the table after the move
    //Using the standardCards collection which store distinct cards with all values in hand,
    //it determines what card(s) will be able to capture all remaining cards.
                       //To improve efficiency, it filter the collection before finding moves.
                          //The conditions are that the value in hand of the card played must be higher that the card with highest value on table,
                          //and that the sum of the cards on table must be divisible by the value in hand.
    val currentStandard = Card.standardCards.filter( card => !(card.valueInHand < remainingCards.maxBy( _.numVal ).numVal)
                                              && ((remainingCards.map( _.numVal ).sum % card.valueInHand) == 0) )
                                                  //sum here does not cause error as the sweep situation has been excluded.

    //This is similar to the strongVerification in Move object. sweepValues in updated if the suitable values are found.
    for (i <- currentStandard.indices) {
      if ((new Finder(remainingCards, Vector(currentStandard(i)))).findAllMoves()
        .exists( _.cardsCaptured.length == remainingCards.length )) sweepValues = sweepValues ++ Vector(currentStandard(i).valueInHand)
    }
    sweepValues //Returns sweepValues
  }

  //This is the only public function of the class, used to return the final "grade" of the score.
  def grade () : Int = {
    //Check if the computer knows all strategies.
    val allWeight = preferences.get("all")
    if (allWeight.nonEmpty) {
      allStrategies.values.foreach( strategy => strategy() ) //If the computer knows all strategies, compute them once.
      //Check for specific points (if any) if that preference is addressed, compute the weight of those points, and store them.
      val specificPoints = preferences.filter( _._1 != "all").map( pair => (pair._1, allPoints(pair._1)*pair._2) )
      //Multiply every points from different strategies with the weight of all.
      allPoints.foreach( pair => allPoints.update(pair._1, pair._2*allWeight.get) )
      //Add the specific points (computed with weight) to all points.
      specificPoints.foreach( pair => updateAllPoints(pair._1, pair._2) )
    }
    else {
      //If the computer only knows specific strategies, only compute those strategies and their weight.
      preferences.foreach( pair => {allStrategies(pair._1)()
                                    allPoints.update(pair._1,allPoints(pair._1)*pair._2)} )
    }
    allPoints.values.sum //Returns the total point from all strategies.
  }

  //This is the function to update the point change from each strategies to their points.
  private def updateAllPoints (key: String, pointChange: Int) : Unit = allPoints.update(key, allPoints(key) + pointChange)
}

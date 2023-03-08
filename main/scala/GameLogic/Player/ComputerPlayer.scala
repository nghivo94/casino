package GameLogic.Player

import Exceptions.IllegalPlayerInfo
import GameLogic.Card.Card
import GameLogic.Move.{Finder, Grader, Move, MoveType}

import scala.collection.mutable.Map
import scala.util.Random.nextInt

class ComputerPlayer (name: String, typeName: String, private val preferences: Map[String, Int], order: Int, score: Int = 0, sweep: Int = 0,
                      hand: Vector[Card] = Vector[Card](), pile: Vector[Card] = Vector[Card](), seen: Vector[Card] = Vector[Card]())
  extends Player (name, typeName,order, score, sweep, hand, pile, seen) {

  //The isComputer always returns true for this subclass.
  def isComputer: Boolean = true

  //This function makes and returns a move based on the given table and the cards on hand.
  def makeMove (opMoveType: Option[MoveType], table: Vector[Card]) : Move = {
    val allMoves = (new Finder(table,giveHand)).findAllMoves() //Finding all possible moves
    (allMoves zip allMoves)
      .map( pair => (pair._1, (new Grader(pair._1,preferences,table,giveHand,giveSeen)).grade() ) ) //Grade the moves
      .maxBy( _._2 )._1 //Choose the moves with the highest points.
  }
}

object ComputerPlayer {
  //List of random names for computer players.
  private val someNames : Vector[String] = Vector("Tonia37@", "UwU", "Jacky7^^%", "TvT", "ImSoBadAtThis",
    "Gladiator", "Anthon112#", "Eskemie$7", "Tom45*",
    "TodayIsAGoodDay", "Zuru**")

  //Even though the strategies are predetermined which all computer players may have access to,
  //behaviours of computer players may differ based on their types, with each type having some different preferences.

  //This system is designed such that the list of types and preferences is limitless,
  //and the creation of new types and modification of existing types is simplified.
  //As such, the existing list here will not include all possible types, but rather demonstrate how this system can achieve its design goal.
  private val typePreferences: Map[String,Map[String,Int]] = Map(
    "fair"      -> Map("all" -> 1),   //a fair computer computes all strategies result with weight 1,
                                      //the keyword for all strategies is "all"
    "dumb"      -> Map(),   //a dumb computer make the first move it can think of, no strategy computation is made for this type.

    //When combining "all" with some specific strategies, it means that those strategies gain additional weights.
    "spade"     -> Map("all" -> 1, "prispade"   -> 1), //total weight of prispade strategy is 2.
    "more"      -> Map("all" -> 1, "primore"    -> 2), //more bias towards a strategy.
    "special"   -> Map("all" -> 3, "prispecial" -> 1), //less bias towards a strategy by increasing weight of all strategies.

    "careful"   -> Map("all" -> 1, "avsweep"  -> 1,  "avpowerplay" -> 1), //can combine with more specific strategies
    "bold"      -> Map("all" -> 2, "avsweep"  -> -1, "avpowerplay" -> -1), //"against" some strategies by giving them negative weights.
    "lookahead" -> Map("all" -> 3, "pribuild" -> 2,  "avpowerplay" -> 1, "avsweep" -> 1), //level of bias can vary between strategies

    //Some types of computer have fewer strategies, and thus only those specific strategies are computed
    "random"    -> Map("prirandom"  -> 1),
    "capture"   -> Map("pricapture" -> 1),
    "immediate" -> Map("prispecial" -> 2, "primore" -> 1, "prisweep" -> 2, "prispade" -> 1)) //specific strategies can also be combined together.

  //Based on in-game performances, types of computer players are grouped in specific modes.
  private val modes: Map[String, Vector[String]] = Map(
    "random"       -> typePreferences.keys.toVector,
    "easy"         -> Vector("dumb", "random", "capture"),
    "intermediate" -> Vector("fair", "careful", "bold", "immediate", "lookahead"),
    "hard"         -> Vector("special", "spade", "more")
  )

  //This is the apply function used for loading players from a file, the type is checked if it is in the listed types.
  def apply (name: String, typeName: String, order: Int, score: Int, sweep: Int,
             hand: Vector[Card], pile: Vector[Card], seen: Vector[Card]) : Player = {
    if (!typePreferences.keys.toVector.contains(typeName.split(" ")(0))) throw IllegalPlayerInfo(s"Invalid player type: ${typeName}.")
    new ComputerPlayer(name, typeName, typePreferences(typeName.split(" ")(0)), order, score, sweep, hand, pile, seen)
  }

  //This is the apply function used for creating new computer players, it chooses a random type based on modes, and a random name if necessary.
  def apply (mode: String, order: Int, name: String = someNames(nextInt(someNames.length))) : Player = {
    val randomType = modes(mode)(nextInt(modes(mode).length))
    new ComputerPlayer(name, randomType + " computer", typePreferences(randomType), order)
  }
}
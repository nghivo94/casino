package GameLogic.Move

//Similar to Suit, there are only 2 types of moves, thus they can be easily managed with case objects.
class MoveType

case object Play    extends MoveType
case object Capture extends MoveType
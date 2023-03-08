package GameLogic.Card

//As there are only 4 suits, class Suit and its case objects are created for easy management and algorithms for some special cards.
//This class only has a suitname as a val.
class Suit (val suitName: String)

case object Heart   extends Suit("Heart")
case object Spade   extends Suit("Spade")
case object Club    extends Suit("Club")
case object Diamond extends Suit("Diamond")
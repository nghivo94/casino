package Exceptions

class CasinoAppException (text: String) extends Exception(text)

class IllegalFile      (text: String) extends CasinoAppException(text)
case class UnknownCard          (text: String) extends IllegalFile(text)
case class IllegalPlayerInfo    (text: String) extends IllegalFile(text)
case class MissingMetaData      (text: String) extends IllegalFile(text)
case class MissingPlayerInfo    (text: String) extends IllegalFile(text)
case class MissingCardDeckInfo  (text: String) extends IllegalFile(text)
case class MissingTableInfo     (text: String) extends IllegalFile(text)
case class IllegalExpression    (text: String) extends IllegalFile(text)
case class MissingCard          (text: String) extends IllegalFile(text)
case class InterruptedFile      (text: String) extends IllegalFile(text)
case class NoPreviousGame       (text: String) extends IllegalFile(text)

class InvalidUserInput (text: String) extends CasinoAppException(text)
case class NoNameChosen         (text: String) extends InvalidUserInput(text)
case class NotEnoughCardsInDeck (text: String) extends InvalidUserInput(text)
case class NoCardInHandChosen   (text: String) extends InvalidUserInput(text)
case class WrongMoveButton      (text: String) extends InvalidUserInput(text)
case class InvalidCapture       (text: String) extends InvalidUserInput(text)
case class NoHumanPlayer        (text: String) extends InvalidUserInput(text)
case class InvalidNumPlayers    (text: String) extends InvalidUserInput(text)
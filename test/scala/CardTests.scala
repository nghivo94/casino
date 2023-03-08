import Exceptions.UnknownCard
import GameLogic.Card.{Card, Club, Diamond, Heart, Spade}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardTests extends AnyFlatSpec with Matchers {
  "Object Card" should "create cards with correct suits." in {
    assert(Card("h3").suit === Heart)
    assert(Card("dj").suit === Diamond)
    assert(Card("c0").suit === Club)
    assert(Card("sq").suit === Spade)
  }

  it should "create cards with correct number values." in {
    assert(Card("h3").numVal === 3)
    assert(Card("d6").numVal === 6)
    assert(Card("c0").numVal === 10)
    assert(Card("dj").numVal === 11)
    assert(Card("ca").numVal === 1)
  }

  it should "create cards with correct names." in {
    assert(Card("h3").giveName === "Heart-3")
    assert(Card("ca").giveName === "Ace of Clubs")
  }

  it should "create cards with correct file paths." in {
    assert(Card("h3").giveImgPath === "file:src/images/3_of_hearts.png")
    assert(Card("ca").giveImgPath === "file:src/images/ace_of_clubs.png")
  }

  it should "throw appropriate exceptions." in {
    assert(intercept[UnknownCard]{Card("h")}.text === "Missing card info.")
    assert(intercept[UnknownCard]{Card("h1")}.text === "Unknown card value: 1.")
    assert(intercept[UnknownCard]{Card("he")}.text === "Unknown card value: e.")
    assert(intercept[UnknownCard]{Card("a3")}.text === "Unknown card suit: a.")
    assert(intercept[UnknownCard]{Card("13")}.text === "Unknown card suit: 1.")
  }
}

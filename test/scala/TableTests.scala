import Exceptions.MissingTableInfo
import GameLogic.Card.{Card, Table}
import GameLogic._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TableTests extends AnyFlatSpec with Matchers {
  val table = new Table
  val testTables = Vector(Some(Vector(Card("h3"),Card("ca"),Card("c5"))), Some(Vector()), Some(Vector(Card("s4"),Card("s4"),Card("d7"))), None)

  "Table" should "load an existing table appropriately." in {
    table.loadTable(testTables(0))
    assert(table.giveTable === testTables(0).get)
    table.loadTable(testTables(1))
    assert(table.giveTable === Vector())
    table.loadTable(testTables(2))
    assert(table.giveTable === Vector(Card("s4"),Card("d7")))
    assertThrows[MissingTableInfo] {table.loadTable(testTables(3))}
  }

  it should "add the cards appropriately." in {
    table.loadTable(testTables(0))
    table.addCards(Vector())
    assert(table.giveTable === testTables(0).get)
    table.addCards(Vector(Card("h3")))
    assert(table.giveTable === testTables(0).get)
    table.addCards(Vector(Card("s2"), Card("h3"), Card("d5")))
    assert(table.giveTable === Vector(Card("h3"),Card("ca"),Card("c5"), Card("s2"), Card("d5")))
  }

  it should "remove the cards appropriately." in {
    table.loadTable(testTables(0))
    table.removeCards(Vector(Card("ca")))
    assert(table.giveTable === Vector(Card("h3"), Card("c5")))
    table.removeCards(Vector(Card("h3"), Card("c5")))
    assert(table.giveTable === Vector())
    table.loadTable(testTables(0))
    table.removeCards(Vector(Card("h3"),Card("c5"), Card("ca")))
    assert(table.giveTable === Vector())
  }

  it should "check for sweep appropriately." in {
    table.loadTable(testTables(1))
    assert(table.hasSweep === true)
    table.loadTable(testTables(2))
    assert(table.hasSweep === false)
    table.removeCards(Vector(Card("s4"),Card("d7")))
    assert(table.hasSweep === true)
  }
}

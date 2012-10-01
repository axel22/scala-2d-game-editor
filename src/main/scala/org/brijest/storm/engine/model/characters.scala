/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package model



import components._
import rules._



/** A basic, most general character.
 *  
 *  Each has a manager which controls what they do - choose their next action
 *  depending on the current state.
 */
@SerialVersionUID(1000L)
abstract class Character extends Entity {
  val pos = access[mutable] cell(Pos(0, 0))
  val dimensions = access[mutable] cell((1, 1))
  
  @inline final def foreachPos[U](f: (Int, Int) => U) {
    var psv = pos()
    var x = psv.x
    var y = psv.y
    val sx = x
    val maxx = x + dimensions()._1
    val maxy = y + dimensions()._2
    while (y < maxy) {
      while (x < maxx) {
        f(x, y)
        x += 1
      }
      x = sx
      y += 1
    }
  }
  
  def positions = new Traversable[(Int, Int)] {
    def foreach[U](f: ((Int, Int)) => U) = foreachPos {
      (x, y) => f((x, y))
    }
  }
  
  def action(area: AreaView) = manager.action(area)
  
  def manager: Manager
  
  def canWalk(from: Slot, to: Slot): Boolean
  
  final def isCharacter = true
  
  final def isItem = false
  
  def foreach(f: Character => Unit) = f(this)
  
  def identifier = this.getClass.getName
  
  def hasTop = false
  
  def topIdentifier = this.getClass.getName + "-top"
  
  def topx = 0
  
  def topy = 0
  
  def shape: Shape = Shape.None

}


object Character {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
}


sealed trait Shape


object Shape {

  case object None extends Shape

  case class Cube(xd: Float, yd: Float, zd: Float, xoff: Float, yoff: Float, zoff: Float) extends Shape

  case class TriPrism(xd: Float, yd: Float, zd: Float, xoff: Float, yoff: Float, zoff: Float) extends Shape

  case class Composite(subs: Seq[Shape]) extends Shape

}

object CharacterSet extends ClassSet[Character] {
  register[characters.meadow.Bush]
  register[characters.meadow.Shrub]
  register[characters.meadow.LargeBush]
  register[characters.meadow.Pepperbush]
  register[characters.meadow.Forsythia]
  register[characters.meadow.Elderberry]
  register[characters.meadow.Sward]
  register[characters.meadow.BurnedBush]
  register[characters.meadow.BurnedShrub]
  
  register[characters.tree.Oak]
  register[characters.tree.YoungOak]
  register[characters.tree.OldOak]
  register[characters.tree.AncientOak]
  
  register[characters.castle.Ivy]
  register[characters.castle.SmallRightIvy]
  register[characters.castle.SmallLeftIvy]
  register[characters.castle.RightBarDoor]
  register[characters.castle.LeftBarDoor]
  register[characters.castle.RightBarFence]
  register[characters.castle.LeftBarFence]
  register[characters.castle.TavernTable]
  register[characters.castle.TavernTableWithCloth]
  register[characters.castle.ArmsShelves]
  register[characters.castle.BowsShelves]
  register[characters.castle.RightTarget]
  register[characters.castle.LeftTarget]
  register[characters.castle.BookshelvesRight]
  register[characters.castle.BookshelvesLeft]
  register[characters.castle.Bookshelves2Right]
  register[characters.castle.Bookshelves2Left]
  register[characters.castle.Bust]
  register[characters.castle.SofaNorth]
  register[characters.castle.SofaWest]
  register[characters.castle.SofaSouth]
  register[characters.castle.SofaEast]
  register[characters.castle.ArmchairNorth]
  register[characters.castle.ArmchairWest]
  register[characters.castle.ArmchairSouth]
  register[characters.castle.ArmchairEast]
  register[characters.castle.SmallTable]
  register[characters.castle.CoffeeTable]
  register[characters.castle.WorkDesk]
  register[characters.castle.DeadPlant]
  register[characters.castle.Cauldron]
  register[characters.castle.LargeOvenLeft]
  register[characters.castle.LargeOvenRight]
  register[characters.castle.KitchenShelvesLeft]
  register[characters.castle.KitchenShelvesRight]
  register[characters.castle.LogPile]
  register[characters.castle.WoodenTub]
  register[characters.castle.DiningTable]
  register[characters.castle.DiningChairNorth]
  register[characters.castle.DiningChairWest]
  register[characters.castle.DiningChairSouth]
  register[characters.castle.DiningChairEast]
  register[characters.castle.StuffedBearLeft]
  register[characters.castle.KnightArmourLeft]
  register[characters.castle.KnightArmourRight]
  register[characters.castle.FireplaceLeft]
  register[characters.castle.FireplaceRight]
  
  register[characters.dungeon.Sarcophagus]
  register[characters.dungeon.Altar]
}


case object NoCharacter extends Character {
  val id = invalidEntityId
  def manager = NoManager
  def pov(a: AreaView) = unsupported()
  def canWalk(from: Slot, to: Slot) = unsupported()
  def chr = '@'
  def color = 0xffffff00
  override def foreach(ev: Character => Unit): Unit = {}
}


/** A character with common ruleset functionality.
 */
trait RulesetCharacter extends Character with Inventory with Stats {
}


/** A regular character.
 *  
 *  Most characters are of this type. A regular character takes 1x1 space.
 */
abstract class RegularCharacter extends RulesetCharacter {
  override def isRC: Boolean = true
  
  def canWalk(from: Slot, to: Slot) = math.abs(from.height - to.height) <= heightStride
}


object RegularCharacter {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[RegularCharacter]) Some(e.id) else None
}


trait OrderCharacter extends RulesetCharacter {
oc =>
  val order = access[mutable].cell[Order](DoNothing)
  val management = access[mutable].cell[Manager](new OrderManager(oc))
  
  def manager = management()
}


object OrderCharacter {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[OrderCharacter]) Some(e.id) else None
}


abstract class PlayerCharacter(val pid: PlayerId, val id: EntityId) extends RegularCharacter with OrderCharacter {
pc =>
  override def isPC: Boolean = true
  def owner = pid
  def pov(area: AreaView) = area // TODO
  def chr = '@'
  def color = 0x0000ff00
}


object PlayerCharacter {
  def simpleTestCharacter(pid: PlayerId) = new PlayerCharacter(pid, (0l, 0l)) with rules.enroute.EnrouteRuleset
}



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
import rules.{Stats, Inventory}



/** A basic, most general character.
 *  
 *  Each has a manager which controls what they do - choose their next action
 *  depending on the current state.
 */
abstract class Character extends Entity {
  val pos = access[mutable] cell(Pos(0, 0))
  val dimensions = access[mutable] cell((1, 1))
  
  def foreachPos[U](f: (Int, Int) => U) {
    var Pos(x, y) = pos()
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
  
  def action(area: AreaView) = manager.action(area)
  
  def manager: Manager
  
  def canWalk(from: Slot, to: Slot): Boolean
  
  final def isCharacter = true
  
  final def isItem = false
  
  def foreach(f: Character => Unit) = f(this)
}


object Character {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
}


case object NoCharacter extends Character {
  val id = invalidEntityId
  def manager = NoManager
  def pov(a: AreaView) = unsupported()
  def canWalk(from: Slot, to: Slot) = unsupported()
  def chr = '@'
  def color = 0xffffff00
  override def foreach(ev: Character => Unit): Unit = {}
  def identifier = "basic_chars.no_character"
}


/** A character with common ruleset functionality.
 */
abstract class RulesetCharacter extends Character {
  def stats: Stats
  def inventory: Inventory
}


/** A regular character.
 *  
 *  Most characters are of this type. A regular character takes 1x1 space.
 */
abstract class RegularCharacter extends RulesetCharacter {
  override def isRC: Boolean = true
  
  def canWalk(from: Slot, to: Slot) = math.abs(from.height - to.height) <= stats.heightStride
}


object RegularCharacter {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[RegularCharacter]) Some(e.id) else None
}


abstract class OrderCharacter extends RegularCharacter {
oc =>
  val order = access[mutable].cell[Order](DoNothing)
  val management = access[mutable].cell[Manager](new OrderManager(oc))
  
  def manager = management()
}


object OrderCharacter {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[OrderCharacter]) Some(e.id) else None
}


case class PlayerCharacter(pid: PlayerId, id: EntityId)(rs: rules.RuleSet) extends OrderCharacter {
pc =>
  
  val stats = rs.newStats
  val inventory = rs.newInventory
  
  override def isPC: Boolean = true
  def owner = pid
  def pov(area: AreaView) = area // TODO
  def chr = '@'
  def color = 0x0000ff00
  def identifier = "basic_chars.playerchar" // TODO
}


object PlayerCharacter {
  def simpleTestCharacter(pid: PlayerId)(rs: rules.RuleSet) = new PlayerCharacter(pid, (0l, 0l))(rs)
}



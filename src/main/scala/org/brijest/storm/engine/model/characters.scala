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
  
}


object Character {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
}


object CharacterSet extends ClassSet[Character] {
  register[characters.meadow.Bush]
  register[characters.meadow.Shrub]
  register[characters.meadow.Pepperbush]
  register[characters.meadow.Forsythia]
  register[characters.meadow.Elderberry]
  register[characters.meadow.Sward]
  register[characters.meadow.BurnedBush]
  register[characters.meadow.BurnedShrub]
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



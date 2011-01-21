package org.brijest.storm.engine
package model



import org.triggerspace._
import util.pathfinding.Path



/** A regular character.
 *  
 *  Most characters are of this type. A regular character takes 1x1 space.
 */
abstract class RegularCharacter(i: EntityId, t: Transactors) extends Character(i, t) {
  
}


object RegularCharacter {
  
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[RegularCharacter]) Some(e.id) else None
  
}
















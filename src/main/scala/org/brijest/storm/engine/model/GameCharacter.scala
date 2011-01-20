package org.brijest.storm.engine
package model



import org.triggerspace._
import util.pathfinding.Path



abstract class RegularCharacter(i: EntityId, t: Transactors) extends Character(i, t) {
  
}


object RegularCharacter {
  
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[RegularCharacter]) Some(e.id) else None
  
}
















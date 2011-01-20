package org.brijest.storm.engine
package model



import org.triggerspace._



abstract class Character(i: EntityId, t: Transactors) extends Entity(i, t) with BasicStats {
  
  val position = cell(Pos(0, 0))
  
  val dimensions = cell((1, 1))
  
}


object Character {
  
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
  
}


trait BasicStats extends Trait {
  
  val speed = cell(50)
  
}




package org.brijest.storm.engine
package model



import org.triggerspace._



trait CharacterView extends Trait {
  def position: immutable.Cell[Pos]
  def dimensions: immutable.Cell[(Int, Int)]
}


/** A basic, most general character.
 *  
 *  They have controllers which control what they do - choose their next action
 *  depending on the current state.
 */
abstract class Character(i: EntityId, t: Transactors) extends Entity(i, t) with BasicStats with CharacterView {
  
  val position = cell(Pos(0, 0))
  
  val dimensions = cell((1, 1))
  
  def action(area: Area)(implicit ctx: Ctx) = manager.action(area)
  
  def manager: Manager
  
}


object Character {
  
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
  
}


trait BasicStats extends Trait {
  
  val speed = cell(50)
  
}




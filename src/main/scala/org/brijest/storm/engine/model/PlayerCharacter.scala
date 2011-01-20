package org.brijest.storm.engine
package model



import org.triggerspace._
import util.pathfinding.Path


case class PlayerCharacter(i: EntityId)(t: Transactors) extends RegularCharacter(i, t) {
  
  val order = cell[Order](DoNothing)
  
  def action(area: Area)(implicit ctx: Ctx): (Action, Option[Int]) = {
    (order().apply(this, area), Some(speed()))
  }
  
}


trait Order extends ImmutableValue {
  def apply(gc: PlayerCharacter, area: Area)(implicit ctx: Ctx): Action
}


case object DoNothing extends Order {
  def apply(gc: PlayerCharacter, area: Area)(implicit ctx: Ctx) = NoAction
}


case class Move(path: Path, destination: Pos) extends Order {
  def apply(gc: PlayerCharacter, area: Area)(implicit ctx: Ctx) = {
    // check if walkable
    
    // if not, recompute path
    
    NoAction
  }
}






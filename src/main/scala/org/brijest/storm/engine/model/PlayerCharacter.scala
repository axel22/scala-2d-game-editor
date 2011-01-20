package org.brijest.storm.engine
package model



import org.triggerspace._
import util.pathfinding.Path


case class PlayerCharacter(i: EntityId)(t: Transactors) extends RegularCharacter(i, t) {
  
  val order = cell[Order](DoNothing)
  
  def action(area: Area): (Action, Option[Int]) = {
    (order().apply(this, area), Some(speed()))
  }
  
}


trait Order extends ((PlayerCharacter, Area) => Action) with ImmutableValue


case object DoNothing extends Order {
  def apply(gc: PlayerCharacter, area: Area) = NoAction
}


case class Move(path: Path) extends Order {
  def apply(gc: PlayerCharacter, area: Area) = {
    NoAction // TODO
  }
}






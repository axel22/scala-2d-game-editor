package org.brijest.storm.engine
package model



import org.triggerspace._
import util.pathfinding.Path


case class GameCharacter(i: EntityId)(t: Transactors) extends Character(i, t) {
  
  val order = cell[Order](DoNothing)
  
  def action(area: Area)(implicit ctx: Ctx): (Action, Option[Int]) = {
    (order().apply(this, area), Some(speed()))
  }
  
}


trait Order extends ((GameCharacter, Area) => Action) with ImmutableValue


case object DoNothing extends Order {
  def apply(gc: GameCharacter, area: Area) = NoAction
}


case class Move(path: Path) extends Order {
  def apply(gc: GameCharacter, area: Area) = {
    NoAction // TODO
  }
}






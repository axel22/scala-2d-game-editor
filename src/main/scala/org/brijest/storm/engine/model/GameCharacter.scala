package org.brijest.storm.engine
package model



import org.triggerspace._
import util.pathfinding.Path


case class GameCharacter(i: EntityId)(m: Models) extends Character(i, m) {
  
  val order = cell[Option[Order]](None)
  
  def action(area: Area): (Action, Option[Int]) = {
    (NoAction, None)
  }
  
}


trait Order extends ((GameCharacter, Area) => Action) with ImmutableValue


case class Move(path: Path) extends Order {
  def apply(gc: GameCharacter, area: Area) = {
    NoAction // TODO
  }
}



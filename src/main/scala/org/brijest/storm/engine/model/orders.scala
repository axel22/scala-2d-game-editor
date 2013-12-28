package org.brijest.storm.engine
package model



import util.pathfinding.Path
import Action._



/* orders */

trait Order extends Immutable {
  def apply(c: Character, area: AreaView): (Action, Order)
}


case object DoNothing extends Order {
  def apply(c: Character, area: AreaView) = (NoAction, DoNothing)
}


case class MoveAlongPath(path: Path) extends Order {
  def apply(c: Character, area: AreaView) = {
    val pos = c.pos()
    if (path.hasNext) {
      val next = path.next(pos)
      val from = area.terrain(pos.x, pos.y)
      val to = area.terrain(next.x, next.y)
      if (area.isWalkable(next) && c.canWalk(from, to)) (moverc(pos, next), MoveAlongPath(path.tail))
      else (haltoc(c.id), DoNothing) // maybe we'll be smarter about this later
    } else (haltoc(c.id), DoNothing)
  }
}


case class Move(direction: Dir) extends Order {
  def apply(c: Character, area: AreaView) = {
    val pos = c.pos()
    val next = pos.to(direction)
    val from = area.terrain(pos.x, pos.y)
    val to = area.terrain(next.x, next.y)
    if (area.isWalkable(next) && c.canWalk(from, to)) (moverc(pos, next), DoNothing)
    else (haltoc(c.id), DoNothing)
  }
}



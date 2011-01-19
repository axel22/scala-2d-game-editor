package org.brijest.storm
package engine



import scala.annotation.switch
import org.triggerspace.ImmutableValue



package object model {
  
  /* area */
  
  type AreaId = Long
  
  def invalidAreaId = 0L

  /* entity */
  
  type EntityId = Long
  
  def invalidEntityId = 0L
  
  /* various types */
  
  trait Action extends ((Area, Entity) => Unit) with ImmutableValue
  
  object NoAction extends Action {
    def apply(a: Area, e: Entity) {}
  }
  
  type Direction = Int
  
  object Direction {
    def north = 8
    def northwest = 7
    def northeast = 9
    def east = 6
    def west = 4
    def south = 2
    def southwest = 1
    def southeast = 3

    def from(pos: Pos, dir: Direction): Pos = from(pos.x, pos.y, dir)
    def from(x: Int, y: Int, dir: Direction): Pos = (dir: @switch) match {
      case 8 => Pos(x, y - 1)
      case 7 => Pos(x - 1, y - 1)
      case 9 => Pos(x + 1, y - 1)
      case 4 => Pos(x - 1, y)
      case 6 => Pos(x + 1, y)
      case 2 => Pos(x, y + 1)
      case 1 => Pos(x - 1, y + 1)
      case 3 => Pos(x + 1, y + 1)
      case _ => illegalarg("Invalid direction " + dir)
    }
  }
  
}

package org.brijest.storm.engine
package util
package pathfinding



import model._



case class Path(lst: List[Direction]) {
  
  def next(pos: Pos): Option[Pos] = lst match {
    case x :: xs => Some(pos.to(x))
    case Nil => None
  }
  
  def tail() = Path(lst.tail)
  
}



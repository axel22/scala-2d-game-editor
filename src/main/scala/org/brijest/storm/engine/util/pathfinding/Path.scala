package org.brijest.storm.engine
package util
package pathfinding



import model._



case class Path(lst: List[Dir]) {
  
  def hasNext = lst != Nil
  
  def next(pos: Pos): Pos = lst match {
    case x :: xs => pos.to(x)
    case Nil => throw new NoSuchElementException
  }
  
  def tail = Path(lst.tail)
  
}



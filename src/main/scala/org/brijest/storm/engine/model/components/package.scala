/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model






package object components {
  
  def cell[T](v: T) = new Cell[T](v)
  def quad[T](w: Int, h: Int, default: (Int, Int) => Option[T]) = new Quad(w, h, default)
  def quad[T](w: Int, h: Int, d: Option[T]): Quad[T] = quad(w, h, (x, y) => d)
  def table[K, V] = new Table[K, V]
  def queue[T] = new Queue[T]
  def heap[T: Ordering] = new Heap[T]
  
}

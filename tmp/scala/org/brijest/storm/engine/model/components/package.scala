/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model






package object components {
  
  def cell[T] = new Cell[T]()
  def cell[T](v: T) = Cell[T](v)
  def quad[T](w: Int, h: Int, default: (Int, Int) => Option[T]): Quad[T]
  def quad[T](w: Int, h: Int, d: Option[T]): Quad[T] = quad(w, h, (x, y) => d)
  
}

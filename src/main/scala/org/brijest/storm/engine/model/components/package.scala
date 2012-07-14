/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model



import collection._



package object components {
  
  trait Access
  
  trait Factory[Acc <: Access] {
    def cell[T](v: T) = new Cell[T, Acc](v)
    def quad[T](w: Int, h: Int, default: (Int, Int) => Option[T], compress: Boolean) = new Quad[T, Acc](w, h, default, compress)
    def quad[T](w: Int, h: Int, d: Option[T], compress: Boolean = false): Quad[T, Acc] = quad(w, h, (x, y) => d, compress)
    def table[K, V] = new Table[K, V, Acc]
    def queue[T] = new Queue[T, Acc]
    def heap[T: Ordering] = new Heap[T, Acc]
    def pile[T] = new Pile[T, Acc]
  }
  
  object factory extends Factory[Nothing]
  
  def access[Acc <: Access]: Factory[Acc] = factory.asInstanceOf[Factory[Acc]]
  
}


package components {
  
  trait free extends Access
  
  object free {
    implicit val evidence = new free {}
  }
  
  sealed trait const extends Access
  
}

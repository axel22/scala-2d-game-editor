/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model.components



import collection._



package immutable {
  trait UniSet[T] extends Set[T]
}


class UniSet[T, Acc] extends immutable.UniSet[T] with Serializable {
  val s = mutable.HashSet[T]()
  
  @inline final override def size = s.size
  @inline final def iterator = s.iterator
  @inline def contains(v: T) = s.contains(v)
  def -(v: T) = s - v
  def +(v: T) = s + v
  
  @inline final def add(elem: T)(implicit acc: Acc) = s.add(elem)
  @inline final def remove(elem: T)(implicit acc: Acc) = s.remove(elem)
  @inline final def clear()(implicit acc: Acc) = s.clear()
}

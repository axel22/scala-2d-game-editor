package org.brijest.storm.engine.model.components



import collection._



package immutable {
  trait Pile[T] extends Set[T]
}


class Pile[T, Acc] extends immutable.Pile[T] with Serializable {
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

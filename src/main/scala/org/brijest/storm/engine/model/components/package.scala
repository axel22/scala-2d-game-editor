package org.brijest.storm.engine.model



import collection._
import annotation.implicitNotFound



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
    def trie[K, V] = new Trie[K, V, Acc]
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
  
  @implicitNotFound(msg = "The component is not mutable.")
  trait mutable extends Access with Serializable
  
  trait Struct extends Serializable
  
}

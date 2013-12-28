package org.brijest.storm.engine.model.components






package immutable {
  
  trait Cell[+T] {
    def apply(): T
  }
  
}


class Cell[T, Acc](private var v: T) extends immutable.Cell[T] with Serializable {
  def this() = this(null.asInstanceOf[T])
  def apply() = v
  def :=(nv: T)(implicit rq: Acc) = v = nv
  def +=(nv: T)(implicit n: Numeric[T], rq: Acc) = v = n.plus(v, nv)
}


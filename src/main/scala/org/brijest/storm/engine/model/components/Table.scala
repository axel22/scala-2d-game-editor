package org.brijest.storm.engine.model.components



import collection._



package immutable {
  trait Table[K, +V] extends Map[K, V]
}


class Table[K, V, Acc] extends immutable.Table[K, V] with Serializable {
  private var dflt: Option[V] = None
  val mp = new mutable.HashMap[K, V] {
    override def default(key: K): V = dflt.get
  }
  
  def defaultVal = dflt
  def defaultVal_=(opt: Option[V])(implicit acc: Acc) = dflt = opt
  
  @inline final def get(k: K) = mp.get(k)
  @inline final override def size = mp.size
  @inline final def iterator = mp.iterator
  def -(k: K) = mp - k
  def +[U >: V](kv: (K, U)) = mp + kv
  
  @inline final def put(k: K, v: V)(implicit acc: Acc) = mp.put(k, v)
  @inline final def remove(k: K)(implicit acc: Acc) = mp.remove(k)
  @inline final def update(k: K, v: V)(implicit acc: Acc) = mp(k) = v
  @inline final def clear()(implicit acc: Acc) = mp.clear()
}

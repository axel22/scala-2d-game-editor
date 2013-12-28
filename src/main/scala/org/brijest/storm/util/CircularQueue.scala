package org.brijest.storm.util






class CircularQueue[T <: AnyRef](val length: Int) {
  private var pos = 0
  private var array = new Array[AnyRef](length)
  
  def +=(elem: T): this.type = {
    array(pos) = elem
    pos = (pos + 1) % length
    this
  }
  
}

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
  
  trait Queue[+T] extends Seq[T] {
    def front: T
  }
  
}


@serializable class Queue[T] extends immutable.Queue[T] with ElemRef[T] {
  private var start = new UNode[T]
  private var prelast = start
  private var lastnd = start
  private var sz = 0
  
  private def enq(elem: T) {
    val nlast = lastnd.append(elem)
    if (nlast ne lastnd) {
      prelast = lastnd
      lastnd = nlast
    }
    sz += 1
  }
  
  private def deq() = {
    val nstart = start.removeHead(this)
    if (nstart ne start) {
      start = nstart
      if (start eq lastnd) prelast = lastnd
    }
    sz -= 1
    elemr
  }
  
  private def clr() {
    start = new UNode[T]
    prelast = start
    lastnd = start
    sz = 0
  }
  
  final def iterator = start.iterator(x => x)
  final def front = start.head
  final def length = sz
  final def apply(idx: Int) = start.apply(idx)
  final def enqueue(elem: T) = enq(elem)
  final def dequeue() = deq()
  final def clear() = clr()
}


trait ElemRef[T] {
  private[components] var elemr: T = _
}


@serializable private class UNode[T] {
  @inline final def bsz = 32
  
  val array = new Array[AnyRef](bsz)
  var start = 0
  var end = 0
  var next: UNode[T] = null
  
  private def left = end - start
  
  final def apply(idx: Int): T = if (idx <= left) array(start + idx).asInstanceOf[T] else {
    if (next != null) next.apply(idx - left)
    else throw new IndexOutOfBoundsException()
  }
  
  final def copy = {
    val n = new UNode[T]
    Array.copy(array, 0, n.array, 0, bsz)
    n.start = start
    n.end = end
    n.next = next
    n
  }
  
  final def hasMore = start < end || next != null
  
  final def append(elem: T): UNode[T] = {
    array(end) = elem.asInstanceOf[AnyRef]
    end += 1
    if (end < bsz) this else {
      next = new UNode[T]
      next
    }
  }
  
  final def iterator(f: UNode[T] => UNode[T]) = new Iterator[T] {
    private var nd = UNode.this
    private var pos = start
    def hasNext = (nd ne null) && pos < nd.end
    def next = if (hasNext) {
      val elem = nd.array(pos)
      pos += 1
      if (pos == end) {
        nd = f(nd.next)
        pos = 0
      }
      elem.asInstanceOf[T]
    } else Iterator.empty.next
  }
  
  final def head: T = array(start).asInstanceOf[T]
  
  final def removeHead(ref: ElemRef[T]): UNode[T] = if (start < end) {
    ref.elemr = array(start).asInstanceOf[T]
    array(start) = null
    start += 1
    if (start < bsz) this else next
  } else throw new NoSuchElementException
  
}

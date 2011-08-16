/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import collection._



trait Linked[Repr <: AnyRef] {
  var next: Repr = null.asInstanceOf[Repr]
  def reset()
}


class MemoryPool[T <: Linked[T]: ClassManifest](newObject: =>T) {
  private var chain = mutable.UnrolledBuffer[T]()
  
  def create = if (chain.nonEmpty) {
    val elem = chain(0)
    if (elem.next eq null) chain.remove(0)
    else chain(0) = elem.next
    elem.next = null.asInstanceOf[T]
    elem.reset()
    elem
  } else newObject
  
  def dispose(obj: T) = if (chain.nonEmpty) {
    if (obj.next eq null) {
      obj.next = chain(0)
      chain(0) = obj
    } else chain += obj
  } else chain += obj
  
}

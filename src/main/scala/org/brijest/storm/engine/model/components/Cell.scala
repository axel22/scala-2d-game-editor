/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model.components






package immutable {
  
  trait Cell[+T] {
    def apply(): T
  }
  
}


@serializable class Cell[T](private var v: T) extends immutable.Cell[T] {
  def this() = this(null.asInstanceOf[T])
  def apply() = v
  def :=(nv: T) = v = nv
  def +=(nv: T)(implicit n: Numeric[T]) = v = n.plus(v, nv)
}

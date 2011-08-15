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
  trait Table[K, +V] extends Map[K, V]
}


class Table[K, V] extends mutable.HashMap[K, V] with immutable.Table[K, V] with Serializable {
  private var dflt: Option[V] = None
  def defaultVal = dflt
  def defaultVal_=(opt: Option[V]) = dflt = opt
  
  override def default(key: K): V = dflt.get
}

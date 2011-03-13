/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import org.triggerspace._



trait ItemTableView extends Trait {
  def ids: immutable.Table[EntityId, Item]
  def locs: immutable.Table[Pos, Queue[Item]]
}


case class ItemTable(t: Transactors) extends Struct(t) with ItemTableView {
  val ids = table[EntityId, Item]
  val locs = table[Pos, Queue[Item]]
  
  def copyFrom(that: ItemTable)(implicit ctx: Ctx) {
    ids.clear
    for ((eid, it) <- that.ids.iterator) ids.put(eid, it.copy)
    
    locs.clear
    for ((pos, itlist) <- that.locs.iterator) locs.put(pos, itlist.copy)
  }
}



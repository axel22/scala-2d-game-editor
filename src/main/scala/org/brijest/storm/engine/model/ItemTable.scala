/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import components._



trait ItemTableView extends Struct {
  def ids: components.immutable.Table[EntityId, ItemView]
  def locs: components.immutable.Quad[List[ItemView]]
}


class ItemTable(w: Int, h: Int) extends ItemTableView {
  private val dflt = Some(Nil)
  private var rawlocs = quad[List[Item]](w, h, dflt)
  
  val ids = table[EntityId, Item]
  
  def locs = rawlocs
  
  def insert(x: Int, y: Int, it: Item) {
    assert(!ids.contains(it.id))
    
    ids(it.id) = it
    locs(x, y) = it :: locs(x, y)
  }
  
  def resize(w: Int, h: Int) {
    val old = rawlocs
    rawlocs = quad[List[Item]](w, h, dflt)
    old.foreach {
      (x, y, item) => rawlocs(x, y) = item
    }
  }
  
}



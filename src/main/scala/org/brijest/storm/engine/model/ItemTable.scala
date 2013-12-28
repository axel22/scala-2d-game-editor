package org.brijest.storm.engine
package model



import components._



trait ItemTableView extends Struct {
  def ids: components.immutable.Table[EntityId, ItemView]
  def locs: components.immutable.Quad[List[ItemView]]
}


@SerialVersionUID(1000L)
class ItemTable(w: Int, h: Int) extends ItemTableView with mutable {
  private val dflt: Option[List[Item]] = Some(Nil)
  private var rawlocs = access[mutable] quad(w, h, dflt)
  
  val ids = access[mutable].table[EntityId, Item]
  
  def locs = rawlocs
  
  def insert(x: Int, y: Int, it: Item)(implicit m: Area) {
    assert(!ids.contains(it.id))
    
    ids(it.id) = it
    locs(x, y) = it :: locs(x, y)
  }
  
  def resize(w: Int, h: Int)(implicit m: Area) {
    val old = rawlocs
    rawlocs = access[mutable].quad[List[Item]](w, h, dflt)
    old.foreach {
      (x, y, item) => rawlocs(x, y) = item
    }
  }
  
}



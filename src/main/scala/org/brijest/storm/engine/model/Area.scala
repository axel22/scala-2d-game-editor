package org.brijest.storm.engine
package model



import org.triggerspace._



trait CharacterTableView extends Trait {
  def ids: immutable.Table[EntityId, CharacterView]
  def locs: immutable.Table[Pos, CharacterView]
}


case class CharacterTable(t: Transactors) extends Struct(t) with CharacterTableView {
  val ids = table[EntityId, Character]
  val locs = table[Pos, Character]
  
  def copy(that: CharacterTable)(implicit ctx: Ctx) {
    ids.clear
    for ((eid, chr) <- that.ids.iterator) ids.put(eid, chr.copy)
    
    locs.clear
    for ((pos, chr) <- that.locs.iterator) locs.put(pos, ids(chr.id))
  }
}


trait ItemTableView extends Trait {
  def ids: immutable.Table[EntityId, Item]
  def locs: immutable.Table[Pos, List[Item]]
}


case class ItemTable(t: Transactors) extends Struct(t) with ItemTableView {
  val ids = table[EntityId, Item]
  val locs = table[Pos, List[Item]]
  
  def copy(that: ItemTable)(implicit ctx: Ctx) {
    ids.clear
    for ((eid, it) <- that.ids.iterator) ids.put(eid, it.copy)
    
    locs.clear
    for ((pos, itlist) <- that.locs.iterator) locs.put(pos, itlist map { i => ids(i.id) })
  }
}


trait AreaView extends Trait {
self =>
  def id: immutable.Cell[AreaId]
  def terrain: immutable.Matrix[Slot]
  def characters: CharacterTableView
  def items: ItemTableView
  def neighbours: immutable.Table[Pos, AreaId]

  def entities(implicit ctx: Ctx) = characters.ids.iterator.map(_._2) ++ items.ids.iterator.map(_._2)
  def entity(id: EntityId)(implicit ctx: Ctx): Option[EntityView] = characters.ids.get(id) match {
    case None => items.ids.get(id)
    case opt => opt
  } 
  def isWalkable(pos: Pos)(implicit ctx: Ctx) = terrain(pos.x, pos.y).walkable && !characters.locs.contains(pos)
}


case class Area(t: Transactors) extends Struct(t) with AreaView {
  val id = cell(invalidAreaId)
  val terrain = matrix(1, 1, Slot(HardRock, 0))
  val characters = struct(CharacterTable)
  val items = struct(ItemTable)
  val neighbours = table[Pos, AreaId]
  
  /* methods */
  
  def copy(area: Area)(implicit ctx: Ctx): Unit = {
    // id
    id := area.id()
    
    // area
    terrain copy area.terrain
    
    // characters
    characters copy area.characters
    
    // items
    items copy area.items
    
    // neighbours
    for ((p, aid) <- neighbours.iterator) neighbours.put(p, aid)
  }
  
}





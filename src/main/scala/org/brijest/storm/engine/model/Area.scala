package org.brijest.storm.engine
package model



import org.triggerspace._



trait AreaView extends Trait {
  val id: immutable.Cell[AreaId]
  val terrain: immutable.Matrix[Slot]
  val characters: immutable.Table[EntityId, CharacterView]
  val characterlocs: immutable.Table[Pos, CharacterView]
  val items: immutable.Table[EntityId, ItemView]
  val itemlocs: immutable.Table[Pos, List[ItemView]]
  val neighbours: immutable.Table[Pos, AreaId]
}


case class Area(t: Transactors) extends Struct(t) with AreaView {
  
  /* fields */
  
  val id = cell(invalidAreaId)
  
  val terrain = matrix(1, 1, Slot(HardRock, 0))
  
  val characters = table[EntityId, Character]
  
  val characterlocs = table[Pos, Character]
  
  val items = table[EntityId, Item]
  
  val itemlocs = table[Pos, List[Item]]
  
  val neighbours = table[Pos, AreaId]
  
  /* methods */
  
  def entities(implicit ctx: Ctx): Iterator[Entity] = characters.iterator.map(_._2) ++ items.iterator.map(_._2)
  
  def entity(id: EntityId)(implicit ctx: Ctx): Option[Entity] = characters.get(id) match {
    case None => items.get(id)
    case opt => opt
  }
  
  def isWalkable(pos: Pos)(implicit ctx: Ctx): Boolean = terrain(pos.x, pos.y).walkable && !characterlocs.contains(pos)
  
  def copy(area: Area)(implicit ctx: Ctx): Unit = {
    // id
    id := area.id()
    
    // area
    terrain copy area.terrain
    
    // characters
    characters.clear
    for ((eid, chr) <- area.characters.iterator) characters.put(eid, chr.copy)
    
    // characterlocs
    characterlocs.clear
    for ((pos, chr) <- area.characterlocs.iterator) characterlocs.put(pos, characters(chr.id))
    
    // items
    items.clear
    for ((eid, it) <- area.items.iterator) items.put(eid, it.copy)
    
    // itemlocs
    itemlocs.clear
    for ((pos, itlist) <- area.itemlocs.iterator) itemlocs.put(pos, itlist map { i => items(i.id) })
    
    // neighbours
    for ((p, aid) <- neighbours.iterator) neighbours.put(p, aid)
  }
  
}





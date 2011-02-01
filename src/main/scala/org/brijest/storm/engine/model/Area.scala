package org.brijest.storm.engine
package model



import org.triggerspace._



trait AreaView extends Trait {
  def id: immutable.Cell[AreaId]
  def terrain: immutable.Matrix[Slot]
  def characters: immutable.Table[EntityId, CharacterView]
  def characterlocs: immutable.Table[Pos, CharacterView]
  def items: immutable.Table[EntityId, ItemView]
  def itemlocs: immutable.Table[Pos, List[ItemView]]
  def neighbours: immutable.Table[Pos, AreaId]
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
  
  def load(area: Area)(implicit ctx: Ctx): Unit = {
    // id
    id := area.id()
    
    // area
    terrain load area.terrain
    
    // TODO characters
    
    // TODO characterlocs
    
    // TODO items
    
    // TODO itemlocs
    
    // neighbours
    for ((p, aid) <- neighbours.iterator) neighbours.put(p, aid)
  }
  
}





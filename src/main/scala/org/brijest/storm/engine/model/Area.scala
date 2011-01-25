package org.brijest.storm.engine
package model



import org.triggerspace._



case class Area(t: Transactors) extends Struct(t) {
  
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
    // TODO very important
  }
  
}





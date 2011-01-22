package org.brijest.storm.engine
package model



import org.triggerspace._



case class Area(t: Transactors) extends Struct(t) {
  
  /* fields */
  
  val id = cell(invalidAreaId)
  
  val terrain = matrix(1, 1, Slot(HardRock, 0))
  
  val characters = table[EntityId, Character]
  
  val characterlocs = table[Pos, Character]
  
  val items = table[Pos, List[Item]]
  
  val neighbours = table[Pos, AreaId]
  
  /* methods */
  
  def entities(implicit ctx: Ctx): Iterator[Entity] = characters.iterator.map(_._2) ++ items.iterator.flatMap(_._2.iterator)
  
  def isWalkable(pos: Pos)(implicit ctx: Ctx): Boolean = terrain(pos.x, pos.y).walkable && !characterlocs.contains(pos)
  
}





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



trait AreaView extends Trait {
self =>
  def id: immutable.Cell[AreaId]
  def terrain: immutable.Quad[Slot]
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
  val terrain: Quad[Slot] = quad(1, 1, Some(HardRock))
  val characters = struct(CharacterTable)
  val items = struct(ItemTable)
  val neighbours = table[Pos, AreaId]
  
  /* methods */
  
  def copyFrom(area: Area)(implicit ctx: Ctx): Unit = {
    // id
    id := area.id()
    
    // area
    terrain copyFrom area.terrain
    
    // characters
    characters copyFrom area.characters
    
    // items
    items copyFrom area.items
    
    // neighbours
    for ((p, aid) <- neighbours.iterator) neighbours.put(p, aid)
  }
  
}





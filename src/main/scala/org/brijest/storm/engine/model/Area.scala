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



trait AreaView extends Struct {
self =>
  def id: components.immutable.Cell[AreaId]
  def terrain: components.immutable.Quad[Slot]
  def characters: CharacterTableView
  def items: ItemTableView
  def neighbours: components.immutable.Table[Pos, AreaId]
  
  def entities = characters.ids.iterator.map(_._2) ++ items.ids.iterator.map(_._2)
  def entity(id: EntityId): Option[EntityView] = characters.ids.get(id) match {
    case None => items.ids.get(id)
    case opt => opt
  } 
  def isWalkable(pos: Pos) = terrain(pos.x, pos.y).walkable && (characters.locs.apply(pos.x, pos.y) == null)
}


class Area extends AreaView {
  val id = cell(invalidAreaId)
  val terrain: Quad[Slot] = quad(1, 1, Some(HardRock0))
  val characters = new CharacterTable(1, 1)
  val items = new ItemTable(1, 1)
  val neighbours = table[Pos, AreaId]
  
  override def entity(id: EntityId) = super.entity(id).map(_.asInstanceOf[Entity])
}


object Area {
  
  def emptyArea: Area = {
    val area = new Area
    area
  }
  
  def simpleTestArea: Area = {
    val area = new Area
    area.items.insert(0, 0, Item.SimpleTestItem((0L, 0L)))
    area
  }
  
}


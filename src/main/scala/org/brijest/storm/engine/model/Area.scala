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
  def maxheight: Cell[Int]
  def characters: CharacterTableView
  def items: ItemTableView
  def neighbours: components.immutable.Table[Pos, AreaId]
  
  def sidelength = terrain.dimensions._1 max terrain.dimensions._2
  def contains(x: Int, y: Int) = {
    val d = terrain.dimensions
    x >= 0 && y >= 0 && x < d._1 && y < d._2
  }
  def entities = characters.ids.iterator.map(_._2) ++ items.ids.iterator.map(_._2)
  def entity(id: EntityId): Option[EntityView] = characters.ids.get(id) match {
    case None => items.ids.get(id)
    case opt => opt
  } 
  def playerCharacter(playerId: PlayerId): PlayerCharacterView = characters.ids(characters.pcs(playerId)).asInstanceOf[PlayerCharacterView]
  final def isWalkable(pos: Pos): Boolean = isWalkable(pos.x, pos.y)
  final def isWalkable(x: Int, y: Int) = isWalkableTerrain(x, y) && (characters.locs.apply(x, y) == NoCharacter)
  final def isWalkableTerrain(x: Int, y: Int) = terrain(x, y).walkable
}


class Area extends AreaView {
  private val entitycount = cell(0l)
  private var rawterrain: Quad[Slot] = quad(1, 1, Some(HardRock0), true)
  val id = cell(invalidAreaId)
  val characters = new CharacterTable(1, 1)
  val items = new ItemTable(1, 1)
  val neighbours = table[Pos, AreaId]
  val maxheight = cell(20)
  
  def newEntityId() = {
    entitycount += 1
    (id(), entitycount())
  }
  
  def terrain = rawterrain
  
  override def entity(id: EntityId) = super.entity(id).map(_.asInstanceOf[Entity])
  
  def character(id: EntityId): Character = characters.ids(id)
  
  override def playerCharacter(playerId: PlayerId): PlayerCharacter = super.playerCharacter(playerId) match {
    case pc @ PlayerCharacter(_, _) => pc
  }
  
  def character(x: Int, y: Int): Character = characters.locs(x, y)
  
  def character(p: Pos): Character = characters.locs(p.x, p.y)
  
  def insert(x: Int, y: Int, item: Item) {
    items.insert(x, y, item)
  }
  
  def insert(c: Character) {
    c.foreachPos((x, y) => assert(isWalkable(x, y), (x, y)))
    characters.insert(c)
  }
  
  def move(rc: RegularCharacter, to: Pos): Unit = assert(tryMove(rc, to))
  
  def tryMove(rc: RegularCharacter, to: Pos) = {
    assert(characters.ids contains rc.id)
    
    if (!isWalkable(to)) false else {
      val from = rc.pos()
      characters.locs.remove(from.x, from.y)
      characters.locs(to.x, to.y) = rc
      rc.pos := to
      true
    }
  }
  
  def resize(w: Int, h: Int) {
    characters.resize(w, h)
    items.resize(w, h)
    val old = rawterrain
    rawterrain.dimensions = (w, h);
    old.foreach {
      (x, y, t) => rawterrain(x, y) = t
    }
  }
  
}


object Area {
  
  def emptyArea: Area = {
    val area = new Area
    area.terrain.default
    area
  }
  
  def simpleTestArea: Area = {
    val area = new Area
    area.insert(0, 0, Item.SimpleTestItem((0L, 0L)))
    area
  }
  
  def emptyDungeon(w: Int, h: Int): Area = {
    val area = new Area
    
    area.resize(w, h);
    area.terrain.default = (x, y) => Some(DungeonFloor0);
    for (x <- 0 until w) {
      area.terrain(x, 0) = HardRock0
      area.terrain(x, h - 1) = HardRock0
    }
    for (y <- 1 until (h - 1)) {
      area.terrain(0, y) = HardRock0
      area.terrain(w - 1, y) = HardRock0
    }
    
    area
  }
  
}


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



class AreaView extends Struct with MutableEvidence {
  private val entitycount = access[mutable] cell(0l)
  private var rawterrain = access[mutable].quad[Slot](1, 1, Some(new HardRock), true)
  val id = access[mutable] cell(invalidAreaId)
  val characters = new CharacterTable(1, 1)
  val items = new ItemTable(1, 1)
  val neighbours = access[mutable].table[Pos, AreaId]
  val maxheight = access[mutable] cell(20)
  
  def sidelength = terrain.dimensions._1 max terrain.dimensions._2
  
  def contains(x: Int, y: Int) = {
    val d = terrain.dimensions
    x >= 0 && y >= 0 && x < d._1 && y < d._2
  }
  
  def entities = characters.ids.iterator.map(_._2) ++ items.ids.iterator.map(_._2)
  
  def entity(id: EntityId): Option[Entity] = characters.ids.get(id) match {
    case None => items.ids.get(id)
    case opt => opt
  } 
  
  def playerCharacter(playerId: PlayerId): PlayerCharacter = characters.ids(characters.pcs(playerId)).asInstanceOf[PlayerCharacter]
  
  final def isWalkable(pos: Pos): Boolean = isWalkable(pos.x, pos.y)
  
  final def isWalkable(x: Int, y: Int) = isWalkableTerrain(x, y) && (characters.locs.apply(x, y) == NoCharacter)
  
  final def isWalkableTerrain(x: Int, y: Int) = terrain(x, y).walkable
  
  def newEntityId()(implicit m: mutable) = {
    entitycount += 1
    (id(), entitycount())
  }
  
  def terrain = rawterrain
  
  def character(id: EntityId): Character = characters.ids(id)
  
  def character(x: Int, y: Int): Character = characters.locs(x, y)
  
  def character(p: Pos): Character = characters.locs(p.x, p.y)
  
  def insert(x: Int, y: Int, item: Item)(implicit m: Area) {
    items.insert(x, y, item)
  }
  
  def insert(c: Character)(implicit m: Area) {
    c.foreachPos((x, y) => assert(isWalkable(x, y), (x, y)))
    characters.insert(c)
  }
  
  def move(rc: RegularCharacter, to: Pos)(implicit m: Area): Unit = assert(tryMove(rc, to))
  
  def tryMove(rc: RegularCharacter, to: Pos)(implicit m: Area) = {
    assert(characters.ids contains rc.id)
    
    if (!isWalkable(to)) false else {
      val from = rc.pos()
      characters.locs.remove(from.x, from.y)
      characters.locs(to.x, to.y) = rc
      rc.pos := to
      true
    }
  }
  
  def resize(w: Int, h: Int)(implicit m: Area) {
    characters.resize(w, h)
    items.resize(w, h)
    val old = rawterrain
    rawterrain.dimensions = (w, h);
    old.foreach {
      (x, y, t) => rawterrain(x, y) = t
    }
  }
  
}


class Area extends AreaView with mutable


object Area {
  
  def emptyArea: Area = {
    implicit val area = new Area
    area.terrain.default
    area
  }
  
  def simpleTestArea: Area = {
    implicit val area = new Area
    area.insert(0, 0, Item.SimpleTestItem((0L, 0L)))
    area
  }
  
  def emptyDungeon(w: Int, h: Int): Area = {
    implicit val area = new Area
    
    area.resize(w, h);
    area.terrain.default = (x, y) => Some(new DungeonFloor);
    for (x <- 0 until w) {
      area.terrain(x, 0) = Slot(classOf[HardRock], 4)
      area.terrain(x, h - 1) = Slot(classOf[HardRock], 4)
    }
    for (y <- 1 until (h - 1)) {
      area.terrain(0, y) = Slot(classOf[HardRock], 4)
      area.terrain(w - 1, y) = Slot(classOf[HardRock], 4)
    }
    
    area
  }
  
  def emptyDungeonTest1(w: Int, h: Int): Area = {
    implicit val area = emptyDungeon(w, h)
    
    if (w > 14 && h > 14) {
      def insertrock(x: Int, y: Int, w: Int = 3, h: Int = 3) {
        val rock = new characters.Rock(area.newEntityId(), (w, h))
        rock.pos := Pos(x, y)
        area.insert(rock)
      }
      insertrock(3, 3)
      insertrock(6, 5)
      insertrock(3, 6, 3, 1)
      insertrock(4, 7, 2, 4)
      insertrock(6, 8, 5, 1)
    }
    
    area
  }
  
}


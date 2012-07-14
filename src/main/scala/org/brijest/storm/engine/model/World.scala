/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package model



import collection._
import java.io._



trait World {
  def name: String
  def position(p: Player): AreaId
  def pc(p: Player): PlayerCharacter
  def plane(id: PlaneId): Plane
  def mainPlane: PlaneId
  def area(id: AreaId): Area
  def planeArea(id: PlaneId, x: Int, y: Int) = area(areaId.onPlane(id, x, y))
  def floatingArea(id: PlaneId) = area(areaId.floating(id))
}


object World {
  
  class Default(val name: String, val mainPlane: PlaneId) extends World with Serializable {
    val areas = mutable.Map[AreaId, Area]()
    val planes = mutable.Map[PlaneId, Plane]()
    val players = mutable.Map[Player, (AreaId, PlayerCharacter)]()
    
    def position(p: Player) = players(p)._1
    def pc(p: Player) = players(p)._2
    def plane(id: PlaneId) = planes(id)
    def area(id: AreaId): Area = id match {
      case areaId.onPlane(id, x, y) => plane(id)(x, y)
      case areaId.floating(id) => plane(id)(0, 0)
    }
  }
  
  object Default {
    def serialize(w: Default, os: OutputStream) {
      val oos = new ObjectOutputStream(os)
      oos.writeObject(w)
    }
    def deserialize(is: InputStream): Default = {
      val ois = new ObjectInputStream(is)
      ois.readObject.asInstanceOf[Default]
    }
  }
  
  final class TestWorld extends World {
    def name = "TestWorld"
    def area(id: AreaId) = Area.emptyDungeon(60, 30)
    def position(p: Player) = 0L
    def pc(p: Player): PlayerCharacter = PlayerCharacter.simpleTestCharacter(p.id)
    def plane(id: PlaneId): Plane = unsupported
    def mainPlane: PlaneId = unsupported
    
    private def place(p: Player, area: Area): PlayerCharacter = {
      implicit val a = area
      
      // find a location to place him in
      val pc = p.createPlayerCharacter(area.newEntityId())
      val (w, h) = area.terrain.dimensions
      
      val it = Iterator.range(0, w).flatMap(x => Iterator.range(0, h).map(y => (x, y)))
      while (it.hasNext) {
        val (x, y) = it.next
        if (area.isWalkable(x, y)) {
          pc.pos := Pos(x, y)
          area.insert(pc)
          return pc
        }
      }
      sys.error("could not place character " + area.id())
    }
  }
  
}

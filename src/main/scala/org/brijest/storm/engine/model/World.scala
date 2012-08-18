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
  def plane(id: PlaneId): Option[Plane]
  def mainPlane: PlaneId
  def area(id: AreaId): Option[AreaProvider]
  def area(id: PlaneId, x: Int, y: Int): Option[AreaProvider] = area(areaId(id, x, y))
  def planes: Map[PlaneId, Plane]
  def newPlaneId(): PlaneId
}


object World {
  
  class Default(var name: String) extends World with Serializable {
    private var count = 0
    val areas = mutable.Map[AreaId, AreaProvider]()
    val players = mutable.Map[Player, (AreaId, PlayerCharacter)]()
    val planes = mutable.Map[PlaneId, Plane]()
    var mainPlane: PlaneId = 0
    
    case class DefaultPlane(name: String, size: Int) extends Plane
    
    planes(mainPlane) = new DefaultPlane("Main plane", 1)
    areas(areaId(mainPlane, 0, 0)) = new AreaProvider.Strict(Area.tileTest(24, 24))
    
    def position(p: Player) = players(p)._1
    def pc(p: Player) = players(p)._2
    def plane(id: PlaneId) = planes.get(id)
    def area(id: AreaId) = areas.get(id)
    def newPlaneId() = {
      count += 1
      count
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
    def area(id: AreaId) = Some(new AreaProvider.Strict(Area.emptyDungeon(60, 30)))
    def position(p: Player) = 0L
    def pc(p: Player): PlayerCharacter = PlayerCharacter.simpleTestCharacter(p.id)
    def plane(id: PlaneId) = unsupported()
    def planes = unsupported()
    def mainPlane: PlaneId = unsupported()
    def newPlaneId() = unsupported()
    
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

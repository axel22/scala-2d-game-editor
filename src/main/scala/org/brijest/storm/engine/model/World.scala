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
  
  final class DefaultWorld extends World {
    def name = "D'Falta"
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

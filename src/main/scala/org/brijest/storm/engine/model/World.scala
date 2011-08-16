/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import rules.RuleSet



trait World {
  def name: String
  def ruleset: RuleSet
  def initializeArea(id: AreaId): Area
  def initialPosition(p: Player): AreaId
  def initialPlace(p: Player, area: Area): PlayerCharacter
}


object World {
  
  final class DefaultWorld extends World {
    def name = "D'Falta"
    def ruleset = rules.enroute.EnrouteRuleSet
    def initializeArea(id: AreaId) = Area.emptyDungeon(60, 30)
    def initialPosition(p: Player) = 0L
    def initialPlace(p: Player, area: Area): PlayerCharacter = {
      // find a location to place him in
      val pc = p.createPlayerCharacter(area.newEntityId())(ruleset)
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

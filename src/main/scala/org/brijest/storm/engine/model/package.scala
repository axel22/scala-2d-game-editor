/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine



import scala.annotation.switch
import model.components._



package model {
  case class PlayerId(id: Long) extends Immutable
}


package object model {
  
  /* area */
  
  type AreaId = Long
  
  def invalidAreaId = -1L

  /* entity */
  
  type EntityId = (Long, Long)
  
  def invalidEntityId = (-1L, -1L)
  
  /* player */
  
  def invalidPlayerId = PlayerId(-1L)
  
  def observerPlayerId = PlayerId(-2L)
  
  def defaultPlayerId = PlayerId(0)
  
  /* various types */
  
  type Dir = Int
  
  object Dir {
    def north = 8
    def northwest = 7
    def northeast = 9
    def east = 6
    def west = 4
    def south = 2
    def southwest = 1
    def southeast = 3

    def fromTo(pos: Pos, dir: Dir): Pos = fromTo(pos.x, pos.y, dir)
    def fromTo(x: Int, y: Int, dir: Dir): Pos = (dir: @switch) match {
      case 8 => Pos(x, y - 1)
      case 7 => Pos(x - 1, y - 1)
      case 9 => Pos(x + 1, y - 1)
      case 4 => Pos(x - 1, y)
      case 6 => Pos(x + 1, y)
      case 2 => Pos(x, y + 1)
      case 1 => Pos(x - 1, y + 1)
      case 3 => Pos(x + 1, y + 1)
      case _ => illegalarg("Invalid direction " + dir)
    }
  }
  
}

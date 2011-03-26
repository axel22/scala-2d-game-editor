/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import util.pathfinding.Path
import Action._



/* orders */

trait Order extends Immutable {
  def apply(c: Character, area: AreaView): (Action, Order)
}


case object DoNothing extends Order {
  def apply(c: Character, area: AreaView) = (NoAction, DoNothing)
}


case class MoveAlongPath(path: Path) extends Order {
  def apply(c: Character, area: AreaView) = {
    val pos = c.pos()
    if (path.hasNext) {
      val next = path.next(pos)
      if (area.isWalkable(next)) (moverc(pos, next), MoveAlongPath(path.tail))
      else (haltpc(c.id), DoNothing) // maybe we'll be smarter about this later
    } else (haltpc(c.id), DoNothing)
  }
}






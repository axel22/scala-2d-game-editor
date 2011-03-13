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
import util.pathfinding.Path



trait Orders extends Trait {
  
  val order = cell[Order](DoNothing)
  
}


/* orders */

trait Order extends ImmutableValue {
  def apply(gc: Character with Orders, area: Area)(implicit ctx: Ctx): (Action, Order)
}


case object DoNothing extends Order {
  def apply(gc: Character with Orders, area: Area)(implicit ctx: Ctx) = (NoAction, DoNothing)
}


case class Move(path: Path, destination: Pos) extends Order {
  def apply(gc: Character with Orders, area: Area)(implicit ctx: Ctx) = {
    val pos = gc.position()
    path.nextPos(pos) match {
      case Some(next) =>
        // check if walkable
        if (area.isWalkable(next)) (MoveRegularCharacter(pos, next), Move(path.tail, destination))
        else (HaltPlayerCharacter(gc.id), DoNothing) // TODO maybe we'll be smarter later
      case None => (HaltPlayerCharacter(gc.id), DoNothing)
    }
  }
}






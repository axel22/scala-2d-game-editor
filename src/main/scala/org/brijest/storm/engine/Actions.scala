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



import components._



object Action {
  implicit object ActionOrdering extends Ordering[Action] {
    def compare(a1: Action, a2: Action) = 0
  }
  
  def composite(actions: Action*) = CompositeAction(actions)
  
  def nothing = NoAction
  
  // def halt(id: EntityId) = HaltPlayerCharacter(id)
  
  // def move(from: Pos, to: Pos) = MoveRegularCharacter(from, to)
  
  // def setOrder(id: EntityId, order: Order) = SetOrder(id, order)
  
}


sealed trait Action extends ImmutableValue {
  def apply(a: Area): Unit
}


object NoAction extends Action {
  def apply(a: Area) {}
}


case class CompositeAction(actions: Seq[Action]) extends Action {
  def apply(a: Area) = for (act <- actions) act(a)
}


// case class HaltPlayerCharacter(id: EntityId) extends Action {
//   def apply(a: Area) = a.characters.ids(id) match {
//     case pc @ PlayerCharacter(_, _) => pc.order := DoNothing
//     case c => illegalarg(c)
//   }
// }
  

// case class MoveRegularCharacter(from: Pos, to: Pos) extends Action {
//   def apply(a: Area)(implicit ctx: Ctx) {
//     a.characters.locs(from) match {
//       case rc @ RegularCharacter(_, _) =>
//         if (a.isWalkable(to)) {
//           rc.position := to
//           a.characters.locs.remove(from)
//           a.characters.locs(to) = rc
//         } else illegalarg(to + " is not walkable.")
        
//         rc match {
//           case o: Orders => o.order 
//           case _ => 
//         }
//       case _ => illegalarg(from + ", " + to)
//     }
//   }
// }


// case class SetOrder(id: EntityId, order: Order) extends Action {
//   def apply(a: Area)(implicit ctx: Ctx) = a.characters.ids(id) match {
//     case o: Orders => o.order := order
//     case x => illegalarg(id + " -> " + x)
//   }
// }

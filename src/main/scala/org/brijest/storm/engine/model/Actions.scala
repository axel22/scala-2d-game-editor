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
  
  // higher order
  
  def composite(actions: Action*) = CompositeAction(actions)
  
  def nothing = NoAction
  
  def cond(c: AreaView => Boolean)(action: Action) = Conditional(c, action)
  
  // basic
  
  def haltpc(id: EntityId) = HaltPC(id)
  
  def moverc(from: Pos, to: Pos) = MoveRC(from, to)
  
  def setOrder(id: EntityId, order: Order) = SetOrder(id, order)
  
}


sealed trait Action extends Immutable {
  def apply(a: Area): Unit
}


object NoAction extends Action {
  def apply(a: Area) {}
}


case class CompositeAction(actions: Seq[Action]) extends Action {
  def apply(a: Area) = for (act <- actions) act(a)
}


case class Conditional(c: AreaView => Boolean, action: Action) extends Action {
  def apply(a: Area) = if (c(a)) action(a)
}


case class HaltPC(id: EntityId) extends Action {
  def apply(a: Area) = a.character(id) match {
    case pc: PlayerCharacter => pc.order := DoNothing
    case c => illegalarg(c)
  }
}
  

case class MoveRC(from: Pos, to: Pos) extends Action {
  assert(from adjacent to)
  
  def apply(a: Area) = a.character(from) match {
    case rc: RegularCharacter => a.move(rc, to)
    case _ => illegalarg(from + ", " + to)
  }
}


case class SetOrder(id: EntityId, order: Order) extends Action {
  def apply(a: Area) = a.characters.ids(id) match {
    case pc: PlayerCharacter => pc.order := order
    case x => illegalarg(id + " -> " + x)
  }
}

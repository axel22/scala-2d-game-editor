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
  
  def haltoc(id: EntityId) = HaltOC(id)
  
  def moverc(from: Pos, to: Pos) = MoveRC(from, to)
  
  def setOrder(id: EntityId, order: Order) = SetOrder(id, order)
  
}


sealed trait Action extends Immutable {
  def apply(implicit a: Area): Unit
}


object NoAction extends Action {
  def apply(implicit a: Area) {}
}


case class CompositeAction(actions: Seq[Action]) extends Action {
  def apply(implicit a: Area) = for (act <- actions) act(a)
}


case class Conditional(c: AreaView => Boolean, action: Action) extends Action {
  def apply(implicit a: Area) = if (c(a)) action(a)
}


case class HaltOC(id: EntityId) extends Action {
  def apply(implicit a: Area) = a.character(id) match {
    case oc: OrderCharacter => oc.order := DoNothing
    case c => illegalarg(c)
  }
}
  

case class MoveRC(from: Pos, to: Pos) extends Action {
  assert(from adjacent to)
  
  def apply(implicit a: Area) = a.character(from) match {
    case rc: RegularCharacter => a.move(rc, to)
    case _ => illegalarg(from + ", " + to)
  }
}


case class SetOrder(id: EntityId, order: Order) extends Action {
  def apply(implicit a: Area) = a.characters.ids(id) match {
    case pc: PlayerCharacter => pc.order := order
    case x => illegalarg(id + " -> " + x)
  }
}

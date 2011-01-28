package org.brijest.storm
package engine
package model



import org.triggerspace._




object Action {
  implicit object ActionOrdering extends Ordering[Action] {
    def compare(a1: Action, a2: Action) = 0
  }
}


sealed trait Action extends ImmutableValue {
  def apply(a: Area)(implicit ctx: Ctx): Unit
}


object NoAction extends Action {
  def apply(a: Area)(implicit ctx: Ctx) {}
}


case class HaltPlayerCharacter(id: EntityId) extends Action {
  def apply(a: Area)(implicit ctx: Ctx) = a.characters(id) match {
    case pc @ PlayerCharacter(_) => pc.order := DoNothing
    case c => illegalarg(c)
  }
}
  

case class DisplaceRegularCharacter(from: Pos, to: Pos) extends Action {
  def apply(a: Area)(implicit ctx: Ctx) {
    a.characterlocs(from) match {
      case rc @ RegularCharacter(_) =>
        if (a.isWalkable(to)) {
          rc.position := to
          a.characterlocs.remove(from)
          a.characterlocs(to) = rc
        } else illegalarg(to + " is not walkable.")
      case _ => illegalarg(from + ", " + to)
    }
  }
}



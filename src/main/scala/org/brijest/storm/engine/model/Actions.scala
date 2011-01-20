package org.brijest.storm
package engine
package model



import org.triggerspace._



trait Action extends ImmutableValue {
  def apply(a: Area, e: Entity)(implicit ctx: Ctx): Unit
}


object NoAction extends Action {
  def apply(a: Area, e: Entity)(implicit ctx: Ctx) {}
}
  

case class DisplaceRegularCharacter(from: Pos, to: Pos) extends Action {
  def apply(a: Area, e: Entity)(implicit ctx: Ctx) {
    a.characters(from) match {
      case rc @ RegularCharacter(_) =>
        if (a.isWalkable(to)) {
          rc.position := to
          a.characters.remove(from)
          a.characters(to) = rc
        } else illegalarg(to + " is not walkable.")
      case _ => illegalarg(e)
    }
  }
}



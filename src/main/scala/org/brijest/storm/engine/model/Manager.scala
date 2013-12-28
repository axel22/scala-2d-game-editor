package org.brijest.storm
package engine
package model



import components._



trait Manager {
  def action(area: AreaView): (Action, Trigger)
}


object Manager


object NoManager extends Manager {
  def action(area: AreaView) = unsupported()
}


object IdleManager extends Manager {
  def action(area: AreaView) = (NoAction, NullTrigger)
}


class OrderManager(oc: OrderCharacter) extends Manager {
  def action(area: AreaView) = {
    val (act, nextord) = oc.order().apply(oc, area)
    (Action.composite(SetOrder(oc.id, nextord), act), Sleep(oc.delay))
  }
}

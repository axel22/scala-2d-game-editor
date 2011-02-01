package org.brijest.storm.engine
package model



import org.triggerspace._
import Action._



trait Manager {
  
  def action(area: Area)(implicit ctx: Ctx): (Action, Trigger)
  
}


class OrderManager(c: Character with Orders) extends Manager {
  def action(area: Area)(implicit ctx: Ctx): (Action, Trigger) = {
    val (action, nextOrder) = c.order().apply(c, area)
    (composite(action, setOrder(c.id, nextOrder)), AfterTime(c.speed()))
  }
}

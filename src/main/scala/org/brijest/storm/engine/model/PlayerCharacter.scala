package org.brijest.storm.engine
package model



import org.triggerspace._



case class PlayerCharacter(i: EntityId)(t: Transactors)
extends RegularCharacter(i, t) with Orders {
pc =>
  
  val owner = cell[PlayerId](invalidPlayerId)
  
  def manager = new OrderManager(pc)
  
  def pov(area: AreaView)(implicit ctx: Ctx) = area // TODO
  
}



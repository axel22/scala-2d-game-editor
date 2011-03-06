package org.brijest.storm.engine
package model



import org.triggerspace._



case class PlayerCharacter(pid: PlayerId, i: EntityId)(ii: InstInfo)
extends RegularCharacter(i, ii) with Orders {
pc =>
  
  val owner = cell[PlayerId](invalidPlayerId)
  
  def manager = new OrderManager(pc)
  
  def pov(area: AreaView)(implicit ctx: Ctx) = area // TODO
  
  def instantiateCopy(ii: InstInfo) = new PlayerCharacter(pid, i)(ii)
  
}



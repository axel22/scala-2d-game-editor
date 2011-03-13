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



case class PlayerCharacter(pid: PlayerId, i: EntityId)(ii: InstInfo)
extends RegularCharacter(i, ii) with Orders {
pc =>
  
  val owner = cell[PlayerId](invalidPlayerId)
  
  def manager = new OrderManager(pc)
  
  def pov(area: AreaView)(implicit ctx: Ctx) = area // TODO
  
  def instantiateCopy(ii: InstInfo) = new PlayerCharacter(pid, i)(ii)
  
}



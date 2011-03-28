/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import components._



case class PlayerCharacter(pid: PlayerId, id: EntityId) extends RegularCharacter {
pc =>
  
  val order = cell[Order](DoNothing)
  
  val owner = cell[PlayerId](invalidPlayerId)
  
  val management = cell[Manager](new OrderManager(pc))
  
  val basicstats = cell[BasicStats](BasicStats.withDelay(1))
  
  def manager = management()
  
  def basicStats = basicstats()
  
  override def isPC: Boolean = true
  
  def pov(area: AreaView) = area // TODO
  
}


object PlayerCharacter {
  
  def simpleTestCharacter(pid: PlayerId) = new PlayerCharacter(pid, (0l, 0l))
  
}

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



case class PlayerCharacter(pid: PlayerId, id: EntityId)(rs: rules.RuleSet) extends OrderCharacter {
pc =>
  
  val owner = cell[PlayerId](invalidPlayerId)
  val basicstats = cell[BasicStats](rs.newAttributes)
  
  def basicStats = basicstats()
  override def isPC: Boolean = true
  def pov(area: AreaView) = area // TODO
  def chr = '@'
  def color = 0x0000ff00
}


object PlayerCharacter {
  def simpleTestCharacter(pid: PlayerId)(rs: rules.RuleSet) = new PlayerCharacter(pid, (0l, 0l))(rs)
}

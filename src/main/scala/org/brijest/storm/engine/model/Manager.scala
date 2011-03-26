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



trait Manager {
  def action(area: AreaView): (Action, Trigger)
}


object Manager


object NoManager extends Manager {
  def action(area: AreaView) = unsupported()
}


class OrderManager(pc: PlayerCharacter) extends Manager {
  def action(area: AreaView) = {
    val (act, nextord) = pc.order().apply(pc, area)
    pc.order := nextord
    (act, Sleep(pc.basicstats().delay))
  }
}

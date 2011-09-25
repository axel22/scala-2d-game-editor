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


object IdleManager extends Manager {
  def action(area: AreaView) = (NoAction, NullTrigger)
}


class OrderManager(oc: OrderCharacter) extends Manager {
  def action(area: AreaView) = {
    val (act, nextord) = oc.order().apply(oc, area)
    (Action.composite(SetOrder(oc.id, nextord), act), Sleep(oc.delay))
  }
}

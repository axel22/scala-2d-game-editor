
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

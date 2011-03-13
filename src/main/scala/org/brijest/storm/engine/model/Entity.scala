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



trait EntityView extends Trait {
  def id: EntityId
  def action(area: Area)(implicit ctx: Ctx): (Action, Trigger)
  def pov(area: AreaView)(implicit ctx: Ctx): AreaView
}


abstract class Entity[Repr <: Entity[Repr]](val id: EntityId, ii: InstInfo) extends CopyStruct[Repr](ii) with EntityView

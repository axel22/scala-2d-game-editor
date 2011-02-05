package org.brijest.storm.engine
package model



import org.triggerspace._



trait EntityView extends Trait {
  def id: EntityId
  def action(area: Area)(implicit ctx: Ctx): (Action, Trigger)
  def pov(area: AreaView)(implicit ctx: Ctx): AreaView
}


abstract class Entity[Repr <: Entity[Repr]](val id: EntityId, ii: InstInfo) extends CopyStruct[Repr](ii) with EntityView

package org.brijest.storm.engine
package model



import org.triggerspace._



trait EntityView extends Trait {
  def id: EntityId
  
  def action(area: Area)(implicit ctx: Ctx): (Action, Trigger)
}


abstract class Entity(val id: EntityId, t: Transactors) extends Struct(t) with EntityView

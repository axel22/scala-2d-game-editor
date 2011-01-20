package org.brijest.storm.engine
package model



import org.triggerspace._



abstract class Entity(val id: EntityId, t: Transactors) extends Struct(t) {
  
  def action(area: Area)(implicit ctx: Ctx): (Action, Option[Int])
  
}

package org.brijest.storm.engine
package model



import org.triggerspace._



abstract class Entity(val id: EntityId, m: Models) extends Struct(m) {
  
  def action(area: Area): (Action, Option[Int])
  
}

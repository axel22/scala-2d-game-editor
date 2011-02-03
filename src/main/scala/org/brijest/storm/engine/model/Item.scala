package org.brijest.storm
package engine
package model



import org.triggerspace._



trait ItemView extends Trait


abstract class Item(i: EntityId, t: Transactors) extends Entity(i, t) with ItemView {
  
  override def copy(implicit ctx: Ctx): Item = unsupported()
  
}

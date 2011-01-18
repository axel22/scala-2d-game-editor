package org.brijest.storm.engine



import org.triggerspace.ImmutableValue



package object model {
  
  /* area */
  
  type AreaId = Long
  
  def invalidAreaId = 0L

  /* entity */
  
  type EntityId = Long
  
  def invalidEntityId = 0L
  
  /* actions */
  
  trait Action extends ((Area, Entity) => Unit) with ImmutableValue
  
  object NoAction extends Action {
    def apply(a: Area, e: Entity) {}
  }
  
}

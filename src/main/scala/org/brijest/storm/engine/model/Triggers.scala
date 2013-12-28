package org.brijest.storm.engine
package model






sealed trait Trigger extends Immutable


object Trigger {
  def assertValid(turns: Long) {
    assert(turns > 0)
    assert(turns < (Long.MaxValue - 1))
  }
}


object NullTrigger extends Trigger


final case class Sleep(turns: Long) extends Trigger {
  Trigger assertValid turns
}


final case class Awake(eid: EntityId, after: Long) extends Trigger {
  Trigger assertValid after
}


final case class Transact(t: Transaction) extends Trigger


final case class Composite(trigz: Seq[Trigger]) extends Trigger {
  assert(!trigz.exists({
    case NullTrigger | Composite(_) => true
    case _ => false
  }), "No-trigger or composite trigger cannot be a part of a composite trigger.")
  assert(trigz.count({
    case Sleep(_) => true
    case _ => false
  }) <= 1, "Can have at most 1 sleep trigger within a composite trigger.")
}



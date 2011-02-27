package org.brijest.storm
package engine
package model



import org.triggerspace._



trait Transaction extends ImmutableValue {
  def participants: List[Transactor[_]]
  def transact: Unit
}

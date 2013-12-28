package org.brijest.storm
package engine
package model



import components._



trait Transaction extends Immutable {
  def participants: Seq[AreaId]
  def transact(local: AreaView, others: Map[AreaId, Area]): Map[AreaId, Action]
}

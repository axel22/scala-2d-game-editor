package org.brijest.storm
package engine.model
package rules



import collection._



trait Inventory {
  def items: mutable.Set[Item]
  def equipslots: Seq[String]
  def equipped: mutable.Map[String, Option[Item]]
  
  def totalWeight: Int = items.foldLeft(0)(_ + _.weight)
}

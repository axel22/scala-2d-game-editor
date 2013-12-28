package org.brijest.storm
package engine.model
package rules
package enroute



import collection._



trait EnrouteStats {
  def delay: Int = 20
  def heightStride: Int = 2
  def HP: Int = 10
  def maxHP: Int = 10
  def strength: Int = 10
}


trait EnrouteInventory {
  val items = mutable.Set[Item]()
  val equipped = mutable.Map() ++ (equipslots zip (0 until equipslots.size).map(x => None: Option[Item]))
  def equipslots = Seq("Head", "Neck", "Left hand", "Right hand", "Armor", "Feet")
}


trait EnrouteRuleset extends EnrouteInventory with EnrouteStats {
  def canMove = true
}

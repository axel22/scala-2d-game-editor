/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine.model
package rules
package enroute



import collection._



object EnrouteRuleSet extends RuleSet
with EnrouteStatsRules
with EnrouteInventoryRules
{
  def name = "Enroute Ruleset"
}


trait EnrouteStatsRules {
  def newStats = Stats(newBasicStats.all ++ newMainStats.all ++ newAttributes.all)
  def newBasicStats = Stats(
    'delay -> Nat(20),
    'heightStride -> Nat(2)
  )
  def newMainStats = Stats(
    'HP -> new Fract(10, 10) with Main
  )
  def newAttributes = Stats(
    'ST -> new Nat(10) with Attribute
  )
}


trait EnrouteInventoryRules {
  def newInventory = new Inventory {
    val items = mutable.Set[Item]()
    def equipslots = Seq("Head", "Neck", "Left hand", "Right hand", "Armor", "Feet")
    val equipped = mutable.Map() ++ (equipslots zip (0 until equipslots.size).map(x => None: Option[Item]))
  }
  def canMove(c: Character) = true
  def additionalDelay(c: Character) = 0
}

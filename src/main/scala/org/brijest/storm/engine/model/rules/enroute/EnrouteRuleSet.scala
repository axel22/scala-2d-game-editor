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

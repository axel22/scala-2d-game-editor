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






object EnrouteRuleSet extends RuleSet
with EnrouteStatsRules
{
  def name = "Enroute Ruleset"
}


trait EnrouteStatsRules {
  def newStats = Stats(
    'delay -> Nat(20),
    'heightStride -> Nat(2),
    'HP -> Fract(10, 10)
  )('HP)
}

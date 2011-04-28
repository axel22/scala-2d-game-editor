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
with EnrouteAttributeRules
{
  def name = "Enroute Ruleset"
}


trait EnrouteAttributeRules {
  def newAttributes = Attributes(
    'delay -> 20,
    'heightStride -> 2,
    'hits -> 10,
    'maxhits -> 10
  )
}

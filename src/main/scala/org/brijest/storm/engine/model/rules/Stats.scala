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






trait Stats extends BasicStats {
  def delay: Int
  def heightStride: Int
  def HP: Int
  def maxHP: Int
  def strength: Int
  
  def mainStats = Map(
    "HP" -> HP,
    "MaxHP" -> maxHP
  )
  def attributes = Map(
    "strength" -> strength
  )
}





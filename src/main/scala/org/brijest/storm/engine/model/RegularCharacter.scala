/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model






/** A regular character.
 *  
 *  Most characters are of this type. A regular character takes 1x1 space.
 */
abstract class RegularCharacter extends RulesetCharacter {
  override def isRC: Boolean = true
  
  def canWalk(from: Slot, to: Slot) = math.abs(from.height - to.height) <= stats.heightStride
}


object RegularCharacter {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[RegularCharacter]) Some(e.id) else None
}
















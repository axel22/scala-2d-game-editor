/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import components._



/** A regular character.
 *  
 *  Most characters are of this type. A regular character takes 1x1 space.
 */
abstract class OrderCharacter extends RegularCharacter {
oc =>
  val order = cell[Order](DoNothing)
  val management = cell[Manager](new OrderManager(oc))
  
  def manager = management()
}


object OrderCharacter {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[OrderCharacter]) Some(e.id) else None
}








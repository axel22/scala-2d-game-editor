/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package model



import components._



trait ItemView extends EntityView {
  final def isCharacter = false
  final def isItem = true
}


abstract class Item extends Entity with ItemView {
  val id: EntityId
}


object Item {
  
  case class SimpleTestItem(id: EntityId) extends Item {
    def action(a: AreaView) = (NoAction, Sleep(1));
    def pov(a: AreaView) = a
    def chr = '~'
    def color = 0xffffff00
  }
  
}


object NoItem extends Item {
  val id = invalidEntityId
  def pov(a: AreaView) = unsupported()
  def action(area: AreaView) = unsupported()
  def chr = '?'
  def color = 0xffffff00
}

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



trait EntityView extends Struct {
  def id: EntityId
  def action(area: AreaView): (Action, Trigger)
  def pov(area: AreaView): AreaView
  
  def chr: Char
  def color: Int
  def isCharacter: Boolean
  def isItem: Boolean
  def isRC: Boolean = false
  def isPC: Boolean = false
}


trait Entity extends EntityView with PublicMutable

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



trait Slot extends ImmutableValue {
  def walkable: Boolean
  def seethrough: Boolean
  def height: Int
}


class HardRock(val height: Int) extends Slot {
  def walkable = false
  def seethrough = false
}


class DungeonFloor(val height: Int) extends Slot {
  def walkable = true
  def seethrough = true
}


object HardRock0 extends HardRock(0)


object DungeonFloor0 extends DungeonFloor(0)

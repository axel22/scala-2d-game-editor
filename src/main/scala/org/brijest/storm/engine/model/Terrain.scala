package org.brijest.storm.engine
package model



import org.triggerspace._



trait Slot extends ImmutableValue {
  def walkable: Boolean
  def seethrough: Boolean
  def height: Int
}


object HardRock extends Slot {
  def walkable = false
  def seethrough = false
  def height = 0
}


object DungeonFloor extends Slot {
  def walkable = true
  def seethrough = true
  def height = 0
}



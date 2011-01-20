package org.brijest.storm.engine
package model



import org.triggerspace._



case class Slot(tpe: Terrain, height: Int) extends ImmutableValue {
}


trait Terrain {
  def walkable: Boolean
  def seethrough: Boolean
}


object HardRock extends Terrain {
  def walkable = false
  def seethrough = false
}





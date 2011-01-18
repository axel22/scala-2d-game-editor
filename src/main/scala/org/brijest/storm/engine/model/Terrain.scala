package org.brijest.storm.engine
package model



import org.triggerspace._



trait Terrain {
  def walkable: Boolean
  def seethrough: Boolean
}


case class Slot(tpe: Terrain, height: Int)


object HardRock extends Terrain {
  def walkable = false
  def seethrough = false
}





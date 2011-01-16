package org.brijest.storm.engine
package model



import org.triggerspace._



trait Terrain {
}


object HardRock extends Terrain


case class Slot(tpe: Terrain, height: Int)



package org.brijest.storm.engine.model



import org.triggerspace._



case class Area(m: Models) extends Struct(m) {
  
  val terrain = spatialmap(1, 1, Slot(HardRock, 0))
  
}





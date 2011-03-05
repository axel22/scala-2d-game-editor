package org.brijest.storm.engine.model



import org.triggerspace._



trait Utils extends Transactors {
  
  def defaultArea(): Area = {
    val area = struct(Area)
    val (w, h) = (60, 30);
    
    area.terrain.dimensions = (w, h);
    for (x <- 1 until (w - 1); y <- 1 until (h - 1)) area.terrain(x, y) = DungeonFloor
    for (x <- 0 until w) {
      area.terrain(x, 0) = HardRock
      area.terrain(x, h - 1) = HardRock
    }
    for (y <- 1 until (h - 1)) {
      area.terrain(0, y) = HardRock
      area.terrain(w - 1, y) = HardRock
    }
    
    area
  }
  
}

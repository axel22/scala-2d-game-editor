package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Screens {
  
  var position: (Int, Int)
  
  def updateScreen(lastActions: Iterator[(EntityId, Action)], area: AreaView): Unit
  
}


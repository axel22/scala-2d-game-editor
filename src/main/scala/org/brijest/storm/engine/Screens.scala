package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Screens {
  
  def updateScreen(lastActions: Iterator[(Entity, Action)], area: Area): Unit
  
}


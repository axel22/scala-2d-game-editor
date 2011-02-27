package org.brijest.storm.engine



import model._



trait UI extends Screens with Inputs


trait DelegatedUI extends UI {
  var delegateUI: UI
  
  def position = delegateUI.position
  
  def position_=(p: (Int, Int)) = delegateUI.position_=(p)
  
  def updateScreen(lastActions: Iterator[(EntityId, Action)], area: AreaView) =
    delegateUI.updateScreen(lastActions, area)
  
  def getInputs() = delegateUI.getInputs()
  
}

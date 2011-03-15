/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

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

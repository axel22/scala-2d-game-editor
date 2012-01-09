/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import model._
import collection._



abstract class IsoUI extends IsoCanvas(24) with UI {
  var pos = (0, 0);
  var playerId: PlayerId = invalidPlayerId
  var engine: Option[Engine] = None
  
  def width: Int
  
  def height: Int
  
  def update(actions: Seq[Action], area: AreaView, state: Engine.State) = refresh(area, state)
  
  def message(msg: String) {}
  
}



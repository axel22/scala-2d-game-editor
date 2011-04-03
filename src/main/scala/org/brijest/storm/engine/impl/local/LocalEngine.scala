/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package impl.local



import model._
import collection._



class LocalEngine(config: Config, val player: Player, w: World) extends Engine {
  val playeruis = mutable.ArrayBuffer[UI]()
  @volatile var running = true
  
  class SimulationThread extends Thread("Local simulator") {
    val area = w.initializeArea(w.initialPosition(player))
    val sim = new Simulator(area)
    
    override def run() {
      w.initialPlace(player, area)
      sim.init()
      playeruis.foreach(_.refresh(area))
      
      while (running) {
        val (_, actions) = sim.step()
        playeruis.foreach(_.update(actions, area))
        Thread.sleep(10)
      }
    }
  }
  
  val simthr = new SimulationThread
  
  def start() = simthr.start()
  
  def awaitTermination() = simthr.join()
  
  def listen(ui: UI) = playeruis += ui
  
  def send(m: Engine.Msg) = m match {
    case Engine.End => running = false
    case _ => // drop message
  }
}




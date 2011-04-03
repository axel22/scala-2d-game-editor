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



import model.World



class LocalEngine(config: Config, w: World) extends Engine {
  class SimulationThread extends Thread("Local simulator") {
    override def run() {
      // TODO
    }
  }
  
  val simthr = new SimulationThread
  
  def start() = simthr.start()
  
  def awaitTermination() = simthr.join()
}




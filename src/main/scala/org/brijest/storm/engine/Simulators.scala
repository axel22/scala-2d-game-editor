package org.brijest.storm.engine



import org.triggerspace._



trait Simulators extends Transactors {
  import Simulators._
  
  /* settings */
  
  /* simulator logic */
  
  class Simulator(t: Transactors) extends Transactor.Template[SimulatorInfo](t) {
    val model = struct(SimulatorInfo(_))
    
    def transact() {
      // initialize
      
      repeat {
        // simulator loop
      } until (model.shouldStop())
    }
  }
  
}





object Simulators {
  
  case class SimulatorInfo(m: Models) extends Struct(m) {
    val model = void // TODO
    val clients = void // TODO
    val neighbours = void // TODO
    val paused = cell(false)
    val shouldStop = cell(false)
  }
  
}

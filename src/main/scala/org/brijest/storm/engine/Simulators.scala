package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Simulators extends Transactors {
  import Simulators._
  
  /* settings */
  
  /* simulator logic */
  
  class Simulator(t: Transactors) extends Transactor.Template[Info](t) {
    val model = struct(Info(_))
    
    def transact() {
      // initialize
      
      repeat {
        // simulator loop iteration
      } until (model.shouldStop())
    }
  }
  
}





object Simulators {
  
  case class Info(m: Models) extends Struct(m) {
    val area = struct(Area)
    val clients = set[Transactor[Clients.Info]]
    val neighbours = table[AreaId, Transactor[Info]]
    val paused = cell(false)
    val shouldStop = cell(false)
  }
  
}

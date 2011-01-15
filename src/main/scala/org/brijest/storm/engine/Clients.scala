package org.brijest.storm.engine



import org.triggerspace._
import org.brijest.bufferz._




trait Clients
extends Transactors
   with Models
   with Simulators
   with Buffers // TODO: change to screens, buffers just one impl
{
shell: Shell =>
  import Clients._
  
  /* settings */
  
  def framesPerSecond = 30.0
  def frameLength = 1 / framesPerSecond
  
  /* client logic */
  
  class Client(t: Transactors) extends Transactor.Template[ClientInfo](t) {
    val model = struct(ClientInfo(_))
    
    def transact() {
      // register with necessary core transactor
      
      repeat {
        // wait for new input, but no longer than frameLength
        
        // run pending actions to update model
        
        // update screen
        
        // register with a new core transactor if necessary
        
        // send all commands
      } until (model.shouldStop())
    }
  }
    
}


object Clients {
  
  case class ClientInfo(m: Models) extends Struct(m) {
    val shouldStop = cell(false)
    val model = void // TODO
    val inputs = void // TODO
    val events = void // TODO
    val registeredWith = cell(0) // TODO
  }
  
}











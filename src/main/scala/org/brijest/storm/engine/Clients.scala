package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Clients
extends Transactors
   with Simulators
   with Screens
{
  import Clients._
  
  /* settings */
  
  def framesPerSecond = 30.0
  def frameLength = 1 / framesPerSecond
  
  /* client logic */
  
  class Client(t: Transactors) extends Transactor.Template[Info](t) {
    val model = struct(Info)
    
    def transact() {
      // register with necessary core transactor
      
      repeat {
        // wait for new input, but no longer than frameLength
        
        // run pending actions to update model
        
        // update screen
        
        // clear pending actions
        
        // register with a new core transactor if necessary
        
        // send all commands
      } until (model.shouldStop())
    }
  }
    
}


object Clients {
  
  trait Input extends ImmutableValue
  
  case class Info(t: Transactors) extends Struct(t) {
    val area = struct(Area)
    val inputs = queue[Input]
    val actions = queue[(Entity, Action)]
    val position = cell((0, 0))
    val shouldStop = cell(false)
    val registeredWith = cell[Transactor[Simulators.Info]]
  }
  
}











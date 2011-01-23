package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Clients
extends Transactors
   with Simulators
   with Screens
{
  import Clients._
  
  /* client logic */
  
  class Client(pid: PlayerId)(t: Transactors) extends Transactor.Template[Info](t) {
    val model = struct(Info)
    import model._
    
    def transact() {
      // register with necessary core transactor
      initialize()
      
      repeat {
        // wait for new input, but no longer than frameLength
        inputs.await(frameLengthNanos)
        
        // run pending actions to update area
        updateArea()
        
        // update screen
        updateScreen(actions.iterator, area)
        
        // clear pending actions
        actions.clear()
        
        // register with a new core transactor if necessary
        reregister()
        
        // send all commands
        sendCommands()
      } until (shouldStop())
      
      terminate()
    }
    
    def initialize() {
    }
    
    def updateArea() {
    }
    
    def reregister() {
    }
    
    def sendCommands() {
    }
    
    def terminate() {
    }
  }
    
}


object Clients {
  
  trait Input extends ImmutableValue
  
  case class Info(t: Transactors) extends Struct(t) {
    /* data model */
    val area = struct(Area)
    val actions = queue[(EntityId, Action)]
    
    /* inputs */
    val inputs = queue[Input]
    
    /* client state related */
    val position = cell((0, 0))
    val shouldStop = cell(false)
    
    /* registration */
    val registeredWith = cell[Transactor[Simulators.Info]]
    val pendingRegistration = cell[Option[Transactor[Simulators.Info]]]
  }
  
}











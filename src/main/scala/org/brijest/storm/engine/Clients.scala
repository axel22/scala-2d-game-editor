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
      playerId := pid
      
      // find transactor
      val t = simulatorForPlayer(pid)
      
      // register
      register(t)
    }
    
    def updateArea() = for ((eid, a) <- actions.iterator) a(area)
    
    def reregister() = pendingRegistration() match {
      case Some(t) =>
        unregister()
        register(t)
      case None =>
    }
    
    def sendCommands() = {
      val ins = inputs.iterator.toSeq
      send (registeredWith()) {
        implicit ctx => for (i <- ins) i(ctx)
      }
    }
    
    def terminate() {
      // unregister
      unregister()
    }
    
    def register(s: Transactor[Simulators.Info]) {
      checkout (s) {
        implicit txn =>
        // synchronize area id
        area load s.model.area
        
        s.model.clients.add(thiz)
        registeredWith := s
      }
    }
    
    def unregister() {
      // checkout and set
      val s = registeredWith()
      if (s ne null) checkout (s) {
        implicit txn =>
        s.model.clients.remove(thiz)
        registeredWith := null
      }
    }
  }
  
}


object Clients {
  
  trait Input extends ImmutableValue with (Ctx => Unit)
  
  case class Info(t: Transactors) extends Struct(t) {
    /* data model */
    val area = struct(Area)
    val playerId = cell[PlayerId]
    
    /* updates */
    val actions = queue[(EntityId, Action)]
    
    /* inputs */
    val inputs = queue[Input]
    
    /* client state related */
    val position = cell((0, 0))
    val shouldStop = cell(true)
    
    /* registration */
    val registeredWith = cell[Transactor[Simulators.Info]]
    val pendingRegistration = cell[Option[Transactor[Simulators.Info]]]
  }
  
}











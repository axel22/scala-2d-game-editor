package org.brijest.storm
package engine



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
        updateScreen(appliedActions.iterator, area)
        
        // clear pending actions
        appliedActions.clear()
        
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
    
    def updateArea() = {
      var cont = true
      while (cont && actions.length > 0) {
        val (cnt, eid, a) = actions.front
        if (cnt > actioncount()) cont = false // TODO wrapping around
        else if (cnt == actioncount()) {
          actions.dequeue()
          a(area)
          actioncount += 1
          appliedActions.enqueue((eid, a))
        } else illegalstate(cnt + " < " + actioncount())
      }
    }
    
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
        
        // set action counter
        actioncount := s.model.actioncount()
        
        // register self
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
    val actioncount = cell(0L)
    val actions = priorityQueue[(Long, EntityId, Action)] // TODO wrapped ordering!
    val appliedActions = queue[(EntityId, Action)]
    
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











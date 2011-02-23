package org.brijest.storm
package engine



import com.weiglewilczek.slf4s._
import org.triggerspace._
import model._



trait Clients
extends Transactors
   with Simulators
   with Screens
   with Inputs {
self =>
  import Clients._
  
  /* client logic */
  
  class Client(pid: PlayerId)
  extends Transactor.Template[Info] with Logging {
    def transactors = self
    val model = struct(Info)
    import model._
    import logger._
    
    def transact() {
      // register with necessary core transactor
      initialize()
      
    }
    
    def mainloop(): Unit = blocking (getInputs()) { inputs =>
      // run pending actions to update area
      updateArea()
      
      // update screen
      updateScreen(appliedActions.iterator, area.characters.findFor(pid).pov(area))
      
      // process player inputs
      processInputs(inputs)
      
      // clear pending actions
      appliedActions.clear()
      
      // send all commands
      sendCommands()
      
      // register with a new core transactor if necessary
      reregister()
      
      if (shouldStop()) once {
        terminate()
      } else await(shouldStop, frameLengthNanos) {
        mainloop()
      }
    }
    
    def initialize() {
      debug("Initializing client for player " + pid)
      
      playerId := pid
      
      // find transactor
      val t = simulatorForPlayer(pid)
      
      // register
      register(t)
    }
    
    def processInputs(inputs: Seq[Input]) = {
      // turn inputs into commands
      for (c <- inputs map toCommand) commands.enqueue(c)
    }
    
    def updateArea() = {
      debug("Updating area for client %d - updates %s".format(pid, actions.iterator.mkString(", ")))
      
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
    
    def reregister() = {
      pendingRegistration() match {
        case Some(t) =>
          debug("Reregistration for client %d".format(pid))
          unregister()
          register(t)
        case None =>
      }
      
      if (registeredWith().hasTerminated) register(simulatorForPlayer(pid))
    }
    
    def sendCommands() = {
      debug("Sending commands for client %d - commands: %s.".format(pid, commands.iterator.mkString(", ")))
      
      val comm = commands.iterator.toSeq
      val t = registeredWith()
      send (t) {
        implicit ctx => for (c <- comm) c(t.model.area, ctx)
      }
    }
    
    def terminate() {
      debug("Termination of client %d.".format(pid))
      
      // unregister
      unregister()
    }
    
    def register(s: Transactor[Simulators.Info]) {
      checkout (s) {
        implicit txn =>
        // synchronize area id
        area copyFrom s.model.area
        
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
  
  /* methods */
  
  def toCommand(i: Input): Command = i match {
    case KeyPress(c) => null // TODO critical
    case MouseClick(x, y, b) => null // TODO critical
  }
  
}


object Clients {
  
  trait Command extends ImmutableValue with ((Area, RCtx) => Unit)
  
  case class Info(t: Transactors) extends Struct(t) {
    /* data model */
    val area = struct(Area)
    val playerId = cell[PlayerId]
    
    /* updates */
    val actioncount = cell(0L)
    val actions = priorityQueue[(Long, EntityId, Action)] // TODO wrapped ordering!
    val appliedActions = queue[(EntityId, Action)]
    
    /* commands */
    val commands = queue[Command]
    
    /* client state related */
    val shouldStop = cell(true)
    
    /* registration */
    val registeredWith = cell[Transactor[Simulators.Info]]
    val pendingRegistration = cell[Option[Transactor[Simulators.Info]]]
  }
  
}











package org.brijest.storm.engine



import com.weiglewilczek.slf4s._
import org.triggerspace._
import model._



trait Simulators
extends Transactors
   with Constants
{
  import Simulators._
  
  /* methods */
  
  def simulatorForPlayer(playerCharacterId: PlayerId): Transactor[Info]
  
  def serialize(siminfo: Simulators.Info): Unit
  
  /* simulator logic */
  
  class Simulator(aid: AreaId)(t: Transactors)
  extends Transactor.Template[Info](t) with Logging {
    val model = struct(Info)
    import model._
    import logger._
    
    def transact() {
      initialize()
      
      repeat {
        simulationStep()
        
        resolveTriggers()
        
        notifyClients()
        
        // clear action queue
        actions.clear()
        
        // perform inter-simulator transactions
        // (possibly adding new actions to the queue)
        performTransactions()
        
        // check pause state
        while (paused()) paused.await()
        
        // wait one period
        shouldStop.await(turnLengthNanos)
      } until (shouldStop())
      
      terminate()
    }
    
    def initialize() {
      debug("Initializing simulator for area " + aid)
      
      // install triggers
      for (t <- triggers.iterator) {
        // TODO
      }
    }
    
    def simulationStep() {
      debug("Simulation step for area " + aid)
      
      // get action for current entities
      while (schedule.length > 0 && schedule.front._1 == simtime()) {
        val (_, eid) = schedule.dequeue()
        val Some(e) = area.entity(eid)
        val (act, trig) = e.action(area)
        
        // perform and store action
        act(area)
        actions.enqueue(actioncount(), eid, act)
        actioncount += 1
        
        // install trigger
        trig match {
          case AfterTime(turns) => scheduleEntity(eid, turns)
          case NoTrigger => // the entity will not be simulated anymore
        }        
      }
      
      simtime += 1
    }
    
    def scheduleEntity(eid: EntityId, turns: Int) = schedule enqueue (simtime() + turns, eid)
    
    def resolveTriggers() {
      while (triggers.length > 0) {
        val t = triggers.dequeue()
        // process trigger
        // TODO
      }
    }
    
    def notifyClients() = {
      debug("Notifying clients for area " + aid)
      
      val as = actions.iterator.toSeq
      for (c <- clients.iterator) send (c) {
        implicit ctx => for (a <- as) c.model.actions.enqueue(a)
      }
    }
    
    def performTransactions() = {
      for (t <- transactions.iterator) {
        // preprocess transactions
        // TODO
      }
      
      // checkout and do all transactions at once
      // TODO
      
      transactions.clear()
    }
    
    def terminate() {
      debug("Terminating simulator for area " + aid)
      
      // deinstall triggers
      // TODO
      
      // serialize
      serialize(model)
      
      // inform clients
      for (c <- clients.iterator) send (c) {
        implicit ctx => c.model.shouldStop := (true)
      }
    }
    
  }
  
}





object Simulators {
  
  case class Info(t: Transactors) extends Struct(t) {
    /* data model */
    val area = struct(Area)
    
    /* simulation state */
    val actioncount = cell(0L)
    val actions = queue[(Long, EntityId, Action)]
    val triggers = queue[Trigger]
    val transactions = queue[Transaction]
    val simtime = cell(0L)
    val schedule = priorityQueue[(Long, EntityId)](elemType[(Long, EntityId)], Ordering[(Long, EntityId)].reverse) // TODO change to wrapped ordering
    val paused = cell(false)
    val shouldStop = cell(false)
    
    /* clients */
    val clients = set[Transactor[Clients.Info]]
  }
  
}






package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Simulators
extends Transactors
   with Constants
{
  import Simulators._
  
  /* settings */
  
  /* methods */
  
  def mainCharacterFor(pid: PlayerId): EntityId
  
  def areaFor(playerCharacterId: EntityId): AreaId
  
  def simulatorForArea(aid: AreaId): Transactor[Info]
  
  def simulatorForPlayer(playerCharacterId: PlayerId): Transactor[Info]
  
  def deserialize(aid: AreaId, area: Area, trigs: Queue[Trigger], sched: Queue[List[EntityId]]): Unit
  
  def serialize(area: Area, trigs: Queue[Trigger], sched: Queue[List[EntityId]]): Unit
  
  /* simulator logic */
  
  class Simulator(aid: AreaId)(t: Transactors) extends Transactor.Template[Info](t) {
    val model = struct(Info)
    import model._
    
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
      // fetch and deserialize
      deserialize(aid, area, triggers, schedule)
      
      // install triggers
      for (t <- triggers.iterator) {
        // TODO
      }
    }
    
    def simulationStep() {
      // get action for current entities
      val current = schedule.dequeue()
      for (eid <- current; e <- area.entity(eid)) {
        val (act, trig) = e.action(area)
        
        // perform and store action
        act(area)
        actions.enqueue(eid, act)
        
        // install trigger
        trig match {
          case AfterTime(turns) => scheduleEntity(eid, turns)
          case NoTrigger => // the entity will not be simulated anymore
        }
      }
    }
    
    def scheduleEntity(eid: EntityId, turns: Int) {
      while (schedule.length <= turns) schedule.enqueue(Nil)
      schedule(turns) = eid :: schedule(turns)
    }
    
    def resolveTriggers() {
      while (triggers.length > 0) {
        val t = triggers.dequeue()
        // process trigger
        // TODO
      }
    }
    
    def notifyClients() = {
      val as = actions.iterator.toSeq
      for (c <- clients.iterator) send (c) {
        implicit ctx => for (a <- as) c.model.actions.enqueue(a)(ctx)
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
      // deinstall triggers
      // TODO
      
      // serialize
      serialize(area, triggers, schedule)
    }
    
  }
  
}





object Simulators {
  
  case class Info(t: Transactors) extends Struct(t) {
    /* data model */
    val area = struct(Area)
    
    /* simulation state */
    val actions = queue[(EntityId, Action)]
    val triggers = queue[Trigger]
    val transactions = queue[Transaction]
    val schedule = queue[List[EntityId]]
    val paused = cell(false)
    val shouldStop = cell(false)
    
    /* clients */
    val clients = set[Transactor[Clients.Info]]
  }
  
}






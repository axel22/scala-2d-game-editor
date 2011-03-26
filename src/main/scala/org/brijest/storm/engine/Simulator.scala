/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine



import com.weiglewilczek.slf4s._
import model._
import components._



class Simulator(val area: Area) {
  
  private implicit val timeOrder = WrapAroundTime
  
  private var actioncount: Long = 0L
  private var simtime = zeroTime
  private val eventqueue = heap[(Time, EntityId)]
  private val simulating = set[EntityId]
  
  def init() {
    actioncount = 0L
    simtime = zeroTime
    eventqueue.clear()
    simulating.clear()
    
    for (e <- area.entities) enqueue(e.id, 0)
  }
  
  init()
  
  def time = simtime
  
  def nextEventAt = eventqueue.max._1
  
  def hasNextEvent = eventqueue.nonEmpty
  
  // pre: !simulating(eid)
  private def enqueue(eid: EntityId, t: Time) {
    simulating.add(eid)
    eventqueue.enqueue((simtime + t, eid))
  }
  
  /** Performs one simulation step.
   */
  def step() = {
    val acnt = actioncount
    val actions = queue[Action]
    
    def awake(eid: EntityId, other: EntityId, t: Time) = if (!simulating(other)) enqueue(other, t)
    
    def processTrigger(eid: EntityId, trig: Trigger): Unit = trig match {
      case NullTrigger => simulating.remove(eid)
      case Sleep(t) => eventqueue.enqueue((simtime + t, eid))
      case Awake(other, t) => awake(eid, other, t)
      case Transact(txn) => unsupported() // yet to be implemented
      case Composite(trigz) => for (t <- trigz) processTrigger(eid, t)
    }
    
    def doAction(e: Entity) {
      val (act, trig) = e.action(e.pov(area))
      act(area)
      actions.enqueue(act)
      actioncount += 1
      processTrigger(e.id, trig)
    }
    
    while (eventqueue.nonEmpty && eventqueue.max._1 == simtime) {
      val (_, eid) = eventqueue.dequeue()
      area.entity(eid) match {
        case Some(entity) => doAction(entity)
        case None => simulating.remove(eid) // skip - it left
      }
    }
    
    simtime += 1
    
    (acnt, actions)
  }
  
}



/*
trait Simulators
extends Constants
{
self =>
  import Simulators._
  
  /* methods */
  
  val txtors: Transactors
  
  val config: Config
  
  val world: World
  
  def simulatorForPlayer(pid: PlayerId): Transactor[Info]
  
  protected def playerMovement(pid: PlayerId, from: AreaId, to: AreaId): Unit
  
  protected def saveAndUnregister(id: AreaId, siminfo: Simulators.Info): Unit
  
  val utils: Utils = new Utils {
    val txtors = self.txtors
  }
  
  /* simulator logic */
  
  import txtors._
  
  case class Simulator(aid: AreaId, area: Area, optstate: Option[State])
  extends SimulatorLogic with Logging {
    def transactors = txtors
    
    val model = struct(Info(area, optstate))
    import model._
    import logger._
    import state._
    
    def newEntityId(implicit ctx: ReceiverCtx): (Long, Long) = {
      entityCounter += 1
      (area.id(), entityCounter())
    }
    
    def transact() {
      initialize()
      simulate()
    }
    
    def simulate(): Unit = await (shouldStop, turnLengthNanos) {
      simulationStep()
      
      resolveTriggers()
      
      notifyClients()
      
      // clear action queue
      actions.clear()
      
      // perform inter-simulator transactions
      // (possibly adding new actions to the queue)
      performTransactions()
      
      // check pause state
      if (paused()) pause()
      
      // wait one period
      if (shouldStop()) once {
        terminate()
      } else restart
    }
    
    def pause(): Unit = await (paused) {
      if (paused()) restart else simulate()
    }
    
    def initialize() {
      info("Initializing simulator for area " + aid)
      
      // install triggers
      for (t <- triggers.iterator) {
        // TODO
      }
    }
    
    def notifyClients() = {
      info("Notifying clients for area " + aid)
      
      val as = actions.iterator.toSeq
      for ((_, c) <- clients.iterator) async (c) {
        implicit ctx => for (a <- as) c.model.actions.enqueue(a)
      }
    }
    
    def terminate() {
      info("Terminating simulator for area " + aid)
      
      // deinstall triggers
      // TODO
      
      // serialize
      saveAndUnregister(aid, model)
      
      // inform clients
      for ((_, c) <- clients.iterator) async (c) {
        implicit ctx => c.model.shouldStop := (true)
      }
    }
    
  }
  
}


trait SimulatorLogic extends Transactor.Template[Simulators.Info] with Logging {
  import logger._
  import model._
  import state._
  
  def simulationStep() {
    info("Simulation step for area " + area.id())
    
    // get action for current entities
    while (schedule.size > 0 && schedule.front._1 == simtime()) {
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
        case Transact(t) => // TODO enqueue transaction
        case t => triggers.enqueue(t)
      }        
    }
    
    simtime += 1
  }
  
  private def scheduleEntity(eid: EntityId, turns: Int) = schedule enqueue (simtime() + turns, eid)
  
  def resolveTriggers() {
    while (triggers.length > 0) {
      val t = triggers.dequeue()
      // process trigger
      // TODO
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
}


object Simulators {
  
  case class State(t: Transactors) extends Struct(t) {
    val actioncount = cell(0L)
    val actions = queue[(Long, EntityId, Action)]
    val triggers = queue[Trigger]
    val simtime = cell(0L)
    val schedule = heap[(Long, EntityId)](elemType[(Long, EntityId)], Ordering[(Long, EntityId)].reverse) // TODO change to wrapped ordering
    val entityCounter = cell(0L)
  }
  
  case class Info(a: Area, so: Option[State])(t: Transactors) extends Struct(t) {
    /* data model */
    val area = a
    
    /* simulation state - must be serialized */
    val state = so match {
      case Some(s) => s
      case None => struct(State)
    }
    
    /* helpers */
    val transactions = queue[Transaction]
    val paused = cell(false)
    val shouldStop = cell(false)
    
    /* clients */
    val clients = table[PlayerId, Transactor[Clients.Info]]
  }
  
}
*/





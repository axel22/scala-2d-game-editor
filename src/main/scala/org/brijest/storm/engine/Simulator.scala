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
  
  def nextEventAt = eventqueue.head._1
  
  def hasNextEvent = eventqueue.nonEmpty
  
  // pre: !simulating(eid)
  private def enqueue(eid: EntityId, t: Time) {
    simulating.add(eid)
    eventqueue.enqueue((simtime + t, eid))
  }
  
  def apply(a: Action) = a(area)
  
  def apply(as: Action*) = for (a <- as) a(area)
  
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
    
    while (eventqueue.nonEmpty && eventqueue.head._1 == simtime) {
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






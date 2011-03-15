/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.h2.jdbc._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



abstract class LocalSimulators(val config: Config, val world: World)
extends Simulators
{
self =>
  import txtors._
  
  // database
  val db = new Database(config)
  
  def startClients(): Unit
  
  case class Master() extends Transactor.Template[Registry] {
    def transactors = txtors
    val model = struct(Registry)
    def transact() = {
      for ((pid, areaid) <- db.getPlayerPositions) model.lastknownpositions.put(pid, areaid)
      startClients()
      awaitCond(model.terminateAll) (_() == true) {
        awaitCond(model.actives) (_.size == 0) {
          for ((pid, a) <- model.lastknownpositions.iterator) db.putPlayerPos(pid, a)
        }
      }
    }
    def newPlayer(pid: PlayerId)(implicit ctx: ReceiverCtx): AreaId = {
      val areaid = world.initialPosition(pid)
      val t = forArea(areaid)
      checkout (t) { implicit txn =>
        val entid = t.newEntityId
        world.initialPlace(self, pid, t.model.area, entid)
      }
      areaid
    }
    def revive(id: AreaId)(implicit ctx: ReceiverCtx): Simulator = {
      val t = db.getInfo(id) match {
        case Some((area, state)) =>
          transactor(Simulator(area.id(), area, Some(state)))
        case None =>
          val area = world.initializeArea(self, id)
          transactor(Simulator(area.id(), area, None))
      }
      model.actives.put(id, t)
      t
    }
    def forArea(areaid: AreaId)(implicit ctx: ReceiverCtx): Simulator = master.model.actives.get(areaid) match {
      case Some(t) => t
      case None => master.revive(areaid)
    }
  }
  
  val master = transactor(Master())
  
  def simulatorForPlayer(pid: PlayerId): Transactor[Simulators.Info] = {
    checkout (master) { implicit txn =>
      master.model.lastknownpositions.get(pid) match {
        case Some(areaid) => master.forArea(areaid)
        case None =>
          val areaid = master.newPlayer(pid)
          master.forArea(areaid)
      }
    }
  }
  
  protected def saveAndUnregister(id: AreaId, siminfo: Simulators.Info) {
    checkout (master) { implicit txn =>
      master.model.actives.remove(id)
      db.putInfo(siminfo.area.id(), siminfo.area, siminfo.state)
    }
  }
  
  protected def playerMovement(pid: PlayerId, from: AreaId, to: AreaId) {
    // TODO
  }
  
  case class Registry(t: Txs) extends Struct(t) {
    val actives = table[AreaId, Simulator]
    val lastknownpositions = table[PlayerId, AreaId]
    val terminateAll = cell(false)
  }
  
}





package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.h2.jdbc._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



class LocalSimulators(val config: Config, val world: World)
extends Simulators
   with LockingTransactors
{
self =>
  // database
  val db = new Database(config)
  
  case class Master() extends Transactor.Template[Registry] {
    def transactors = self
    val model = struct(Registry)
    def transact() = await(model.terminateAll) {}
    def newPlayer(pid: PlayerId): AreaId = {
      val areaid = world.initialPosition(pid)
      val t = forArea(areaid)
      checkout (t) { implicit txn =>
        world.initialPlace(pid, t.model.area)
      }
      areaid
    }
    def revive(id: AreaId): Transactor[Simulators.Info] = {
      val t = db.getInfo(id) match {
        case Some((area, state)) =>
          transactor(Simulator(area.id(), area, Some(state)))
        case None =>
          val area = world.initializeArea(id)
          transactor(Simulator(area.id(), area, None))
      }
      model.actives.put(id, t)
      t
    }
    def forArea(areaid: AreaId) = master.model.actives.get(areaid) match {
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
  
  case class Registry(t: Txs) extends Struct(t) {
    val actives = table[AreaId, Transactor[Simulators.Info]]
    val lastknownpositions = table[PlayerId, AreaId]
    val terminateAll = cell(false)
  }
  
}





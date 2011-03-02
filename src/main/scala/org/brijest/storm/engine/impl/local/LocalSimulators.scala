package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.h2.jdbc._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



class LocalSimulators(val config: Config)
extends Simulators
   with LockingTransactors
{
  // database
  val db = new Database(config)
  
  val master = transactor(struct(Registry)) { reg =>
  }
  
  def simulatorForPlayer(pid: PlayerId): Transactor[Simulators.Info] = {
    checkout (master) { implicit txn =>
      master.model.playerpositions.get(pid) match {
        case Some(areaid) => master.model.actives.get(areaid) match {
          case Some(t) => t
          case None => null // TODO revive transactor
        }
        case None => null // TODO place player according to world rules
      }
    }
  }
  
  protected def saveAndUnregister(id: AreaId, siminfo: Simulators.Info) {
    checkout (master) { implicit txn =>
      master.model.actives.remove(id)
      // TODO save transactor state
    }
  }
  
  case class Registry(t: Txs) extends Struct(t) {
    val actives = table[AreaId, Transactor[Simulators.Info]]
    val playerpositions = table[PlayerId, AreaId]
  }
  
}





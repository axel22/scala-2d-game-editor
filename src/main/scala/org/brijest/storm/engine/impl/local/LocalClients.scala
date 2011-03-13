package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



class LocalClients(c: Config, w: World, val txtors: Transactors)
extends LocalSimulators(c, w)
   with Clients
{
  import txtors._
  
  var delegateUI: UI = _
  
  def clientLeft(pid: PlayerId) {
    checkout (master) { implicit txn =>
      master.model.terminateAll := true
    }
  }
  
  def newPlayerId() = PlayerId(0L)
  
  def startClients() = transactor(Client(newPlayerId()))
  
}

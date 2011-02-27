package org.brijest.storm.engine
package impl.local



import com.weiglewilczek.slf4s._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



class LocalSimulators
extends Simulators
   with LockingTransactors
{
  
  def simulatorForPlayer(playerCharacterId: PlayerId): Transactor[Simulators.Info] = {
    null // TODO
  }
  
  protected def saveAndUnregister(siminfo: Simulators.Info) {
    // TODO
  }
  
}

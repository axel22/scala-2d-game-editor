package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



class LocalClients(c: Config, w: World)
extends LocalSimulators(c, w)
   with Clients
{
  var delegateUI: UI = _
  
  def clientLeft(pid: PlayerId) {
    // TODO
  }
  
}

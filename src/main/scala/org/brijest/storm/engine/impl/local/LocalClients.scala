package org.brijest.storm.engine
package impl.local



import com.weiglewilczek.slf4s._
import org.triggerspace._
import org.triggerspace.threadlocking2._
import model._



class LocalClients
extends LocalSimulators
   with Clients
{
  var delegateUI: UI = _
}

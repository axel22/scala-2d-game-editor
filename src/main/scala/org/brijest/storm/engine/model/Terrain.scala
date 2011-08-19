/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import components._
import collection._
import annotation.switch



trait Slot extends Immutable {
  def walkable: Boolean
  def seethrough: Boolean
  def height: Int
  def chr: Char
  def color: Int
  def identifier: String
  
  assert(height >= 0)
  
  def atHeight(h: Int) = Slot(this.getClass, h)
}


object Slot {
  private val cachedslots = mutable.Map[Class[_], mutable.Map[Int, Slot]]()
  
  def apply[T <: Slot: Manifest](h: Int): Slot = apply(implicitly[Manifest[T]].erasure, h)
  
  def apply(cls: Class[_], h: Int) = {
    def newslot = cls.getConstructor(classOf[Int]).newInstance(h.asInstanceOf[AnyRef]).asInstanceOf[Slot]
    cachedslots.get(cls) match {
      case Some(hmap) => hmap.get(h) match {
        case Some(slot) => slot
        case None =>
          val slot = newslot
          hmap.put(h, slot)
          slot
      }
      case None =>
        val slot = newslot
        cachedslots.put(cls, mutable.Map[Int, Slot](h -> slot))
        slot
    }
  }
}


case class HardRock(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = false
  def seethrough = false
  def chr = '#'
  def color = 0x55555500
  def identifier = "dungeon.rock"
}


case class DungeonFloor(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = true
  def seethrough = true
  def chr = '.'
  def color = 0x55555500
  def identifier = "dungeon.floor"
}














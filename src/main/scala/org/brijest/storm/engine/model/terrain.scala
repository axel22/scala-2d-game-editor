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



abstract class Slot extends Immutable {
  val identifier = this.getClass.getName
  protected val identhash = identifier.hashCode
  
  def walkable: Boolean
  def seethrough: Boolean
  def height: Int
  def chr: Char
  def color: Int
  /** Never use 2 different terrain types with the same layer in the same area. */
  def layer: Int
  
  def wallsuffix = "-wall"
  
  def edgesuffix = "-edges"
  
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


object Terrain {
  private val terrains = mutable.Buffer[Class[Slot]]()
  
  def registered: Seq[Class[Slot]] = terrains
  
  def register[T <: Slot: Manifest] = terrains += manifest[T].erasure.asInstanceOf[Class[Slot]]
  
  register[HardRock]
  register[DungeonFloor]
  register[DungeonFungus]
}


object NoSlot extends Slot {
  def walkable = false
  def seethrough = true
  def height = 0
  def chr = '_'
  def color = 0x00000000
  def layer = 0
}


case class HardRock(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = false
  def seethrough = false
  def chr = '#'
  def color = 0x55555500
  def layer = 400
}


case class DungeonFloor(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = true
  def seethrough = true
  def chr = '.'
  def color = 0x55555500
  def layer = 500
}


case class DungeonFungus(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = true
  def seethrough = true
  def chr = '.'
  def color = 0x55555500
  def layer = 600
}














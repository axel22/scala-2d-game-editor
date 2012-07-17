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
  def height: Int
  def chr: Char
  def color: Int
  /** Never use 2 different terrain types with the same layer in the same area. */
  def layer: Int
  
  def wallsuffix = "-wall"
  
  def edgesuffix = "-edges"
  
  def isEmpty = false
  
  assert(height >= 0)
  
  def atHeight(h: Int) = Slot(this.getClass, h)
}


object Slot {
  private val cachedslots = mutable.Map[Class[_], mutable.Map[Int, Slot]]()
  
  def apply[T <: Slot: Manifest](h: Int): Slot = apply(implicitly[Manifest[T]].erasure, h)
  
  def apply(cls: Class[_], h: Int): Slot = {
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
  
  def apply(classname: String, h: Int): Slot = apply(Class.forName(classname), h)
  
  def apply(s: Slot, h: Int): Slot = apply(s.getClass, h)
  
}


object Terrain {
  private val terrains = mutable.Buffer[Class[Slot]]()
  
  def registered: Seq[Class[Slot]] = terrains
  
  def register[T <: Slot: Manifest] = terrains += manifest[T].erasure.asInstanceOf[Class[Slot]]
  
  register[EmptySlot]
  register[HardRock]
  register[DungeonFloor]
  register[DungeonFungus]
  register[DungeonSkeleton]
}


case class EmptySlot(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = false
  def chr = '_'
  def color = 0x00000000
  def layer = 0
  override def isEmpty = true
}


case object NoSlot extends EmptySlot


case class HardRock(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = false
  def chr = '#'
  def color = 0x55555500
  def layer = 400
}


case class DungeonFloor(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = true
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
  def layer = 10600
}


case class DungeonSkeleton(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = true
  def seethrough = true
  def chr = '.'
  def color = 0x55555500
  def layer = 10500
}














/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package model



import components._
import collection._
import annotation.switch



abstract class Slot extends Immutable with Serializable {
  def identifier = this.getClass.getName
  def edgeIdentifier = this.getClass.getName + "-edges"
  def wallIdentifier = this.getClass.getName + "-wall"
  def topIdentifier = classOf[EmptySlot].getName + "-top"
  
  def walkable: Boolean
  def height: Int
  def chr: Char
  def color: Int
  /** Never use 2 different terrain types with the same layer in the same area. */
  def layer: Int
  
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


object Terrain extends Logging {
  private val terrains = mutable.Buffer[Class[Slot]]()
  
  def registered: Seq[Class[Slot]] = terrains
  
  def register[T <: Slot: Manifest] = {
    val cls = manifest[T].erasure.asInstanceOf[Class[Slot]]
    logger.debug("registering terrain: " + cls.getName)
    terrains += cls
  }
  
  register[EmptySlot]
  register[HardRock]
  register[HardRockMoss]
  register[HardRockFungus]
  register[DungeonFloor]
  register[DungeonSkeleton]
  register[DungeonSkeletonLeft]
  register[DungeonShackles]
  register[DungeonMoss]
  register[DungeonBlood]
  register[DungeonFungus]
  register[StoneTiles]
  register[RedCarpet]
  register[MeadowGrassPlain]
  register[MeadowGrass]
  register[MeadowPoppy]
  register[MeadowLobelia]
  register[MeadowSunflower]
}


class EmptySlot(val height: Int) extends Slot {
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


case class HardRockMoss(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[HardRock].getName + "-edges"
  
  def walkable = false
  def chr = '#'
  def color = 0x55555500
  def layer = 10401
}


case class HardRockFungus(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  override def wallIdentifier = classOf[HardRock].getName + "-wall"
  
  def walkable = false
  def chr = '#'
  def color = 0x55555500
  def layer = 10402
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
  def chr = '.'
  def color = 0x55555500
  def layer = 10600
}


case class DungeonSkeleton(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 10525
}


case class DungeonSkeletonLeft(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 10515
}


case class DungeonShackles(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 10520
}


case class DungeonMoss(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  override def wallIdentifier = classOf[DungeonFloor].getName + "-wall"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 10510
}


case class DungeonBlood(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  override def wallIdentifier = classOf[DungeonFloor].getName + "-wall"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 10510
}


case class StoneTiles(val height: Int) extends Slot {
  def this() = this(0)
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 600
}


case class RedCarpet(val height: Int) extends Slot {
  def this() = this(0)
  
  override def wallIdentifier = classOf[StoneTiles].getName + "-wall"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 2000
}


case class MeadowGrass(val height: Int) extends Slot {
  def this() = this(0)
  
  override def topIdentifier = classOf[MeadowGrass].getName + "-top"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 12500
}


case class MeadowPoppy(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  override def wallIdentifier = classOf[MeadowGrass].getName + "-wall"
  override def topIdentifier = classOf[MeadowGrass].getName + "-top"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 12505
}


case class MeadowLobelia(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  override def wallIdentifier = classOf[MeadowGrass].getName + "-wall"
  override def topIdentifier = classOf[MeadowGrass].getName + "-top"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 12506
}


case class MeadowSunflower(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[EmptySlot].getName + "-edges"
  override def wallIdentifier = classOf[MeadowGrass].getName + "-wall"
  override def topIdentifier = classOf[MeadowGrass].getName + "-top"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 12507
}


case class MeadowGrassPlain(val height: Int) extends Slot {
  def this() = this(0)
  
  override def edgeIdentifier = classOf[MeadowGrass].getName + "-edges"
  override def wallIdentifier = classOf[MeadowGrass].getName + "-wall"
  override def topIdentifier = classOf[MeadowGrass].getName + "-top"
  
  def walkable = true
  def chr = '.'
  def color = 0x55555500
  def layer = 12500
}









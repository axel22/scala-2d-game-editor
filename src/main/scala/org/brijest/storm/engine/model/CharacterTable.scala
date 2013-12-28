package org.brijest.storm.engine
package model



import components._



@SerialVersionUID(1000L)
class CharacterTable(w: Int, h: Int) extends mutable with Struct {
  private implicit val m = new mutable {}

  private val dflt = Some(NoCharacter)
  val ids = access[mutable].table[EntityId, Character]
  val locs = access[mutable].quad[Character](w, h, dflt)
  val pcs = access[mutable].table[PlayerId, EntityId]
  
  ids.defaultVal = dflt
  
  def insert(c: Character)(implicit m: Area) {
    assert(!ids.contains(c.id))
    c.foreachPos((x, y) => assert(locs(x, y) == NoCharacter))
    
    ids(c.id) = c
    c.foreachPos((x, y) => locs(x, y) = c)
    c match {
      case pc: PlayerCharacter => pcs(pc.pid) = pc.id
      case _ =>
    }
  }
  
  def remove(c: Character)(implicit m: Area) {
    if (ids.contains(c.id)) {
      ids.remove(c.id)
      c.foreachPos((x, y) => locs.remove(x, y))
      c match {
        case pc: PlayerCharacter => pcs.remove(pc.pid)
        case _ =>
      }
    }
  }
  
  def resize(w: Int, h: Int)(implicit m: Area) {
    locs.dimensions = (w, h)
    
    for ((_, c) <- ids) c.foreachPos((x, y) => locs(x, y) = c)
  }
  
  def apply(x: Int, y: Int) = locs(x, y)
}



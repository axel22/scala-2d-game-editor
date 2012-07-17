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



class CharacterTable(w: Int, h: Int) extends PublicMutable with Struct {
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
      case _ => // do nothing
    }
  }
  
  def resize(w: Int, h: Int)(implicit m: Area) {
    locs.dimensions = (w, h)
    
    for ((_, c) <- ids) c.foreachPos((x, y) => locs(x, y) = c)
  }
  
  def apply(x: Int, y: Int) = locs(x, y)
}



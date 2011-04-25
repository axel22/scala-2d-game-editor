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



trait CharacterTableView extends Struct {
  def ids: components.immutable.Table[EntityId, CharacterView]
  def locs: components.immutable.Quad[CharacterView]
  def pcs: components.immutable.Table[PlayerId, EntityId]
}


class CharacterTable(w: Int, h: Int) extends CharacterTableView {
  private val dflt = Some(NoCharacter)
  val ids = table[EntityId, Character]
  val locs = quad[Character](w, h, dflt)
  val pcs = table[PlayerId, EntityId]
  
  ids.defaultVal = dflt
  
  def insert(c: Character) {
    assert(!ids.contains(c.id))
    c.foreachPos((x, y) => assert(locs(x, y) == NoCharacter))
    
    ids(c.id) = c
    c.foreachPos((x, y) => locs(x, y) = c)
    c match {
      case pc @ PlayerCharacter(plid, id) => pcs(plid) = id
      case _ => // do nothing
    }
  }
  
  def resize(w: Int, h: Int) {
    locs.dimensions = (w, h)
    
    for ((_, c) <- ids) c.foreachPos((x, y) => locs(x, y) = c)
  }
  
}



/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package model



import org.triggerspace._



trait CharacterTableView extends Trait {
  def ids: immutable.Table[EntityId, CharacterView]
  def locs: immutable.Table[Pos, CharacterView]
  
  def findFor(pid: PlayerId)(implicit ctx: Ctx) = ids.iterator map { _._2 } find {
    case pc @ PlayerCharacter(`pid`, _) => true
    case _ => false
  } get
}


case class CharacterTable(t: Transactors) extends Struct(t) with CharacterTableView {
  val ids = table[EntityId, Character]
  val locs = table[Pos, Character]
  
  def put(pos: Pos, c: Character)(implicit ctx: Ctx) {
    ids(c.id) = c
    locs(pos) = c
  }
  
  def copyFrom(that: CharacterTable)(implicit ctx: Ctx) {
    ids.clear
    for ((eid, chr) <- that.ids.iterator) ids.put(eid, chr.copy)
    
    locs.clear
    for ((pos, chr) <- that.locs.iterator) locs.put(pos, ids(chr.id))
  }
}



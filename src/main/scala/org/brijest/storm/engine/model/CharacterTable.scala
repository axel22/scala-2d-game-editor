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
}


class CharacterTable(w: Int, h: Int) extends CharacterTableView {
  private val dflt = Some(NoCharacter)
  val ids = table[EntityId, Character]
  val locs = quad[Character](w, h, dflt)
}



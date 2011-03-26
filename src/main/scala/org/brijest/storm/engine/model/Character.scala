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



trait CharacterView extends EntityView {
  def pos: components.immutable.Cell[Pos]
  def dimensions: components.immutable.Cell[(Int, Int)]
  
  final def isCharacter = true
  final def isItem = false
}


/** A basic, most general character.
 *  
 *  Each has a manager which controls what they do - choose their next action
 *  depending on the current state.
 */
abstract class Character extends Entity with CharacterView {
  val pos = cell(Pos(0, 0))
  val dimensions = cell((1, 1))
  
  def foreachPos(f: (Int, Int) => Unit) {
    var Pos(x, y) = pos()
    val sx = x
    val maxx = x + dimensions()._1
    val maxy = y + dimensions()._2
    while (y < maxy) {
      while (x < maxx) {
        f(x, y)
        x += 1
      }
      x = sx
      y += 1
    }
  }
  
  def action(area: AreaView) = manager.action(area)
  
  def manager: Manager
}


object Character {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
}


case object NoCharacter extends Character {
  val id = invalidEntityId
  def manager = NoManager
  def pov(a: AreaView) = unsupported()
}



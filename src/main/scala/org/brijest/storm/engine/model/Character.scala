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
  def position: components.immutable.Cell[Pos]
  def dimensions: components.immutable.Cell[(Int, Int)]
}


/** A basic, most general character.
 *  
 *  Each has a manager which controls what they do - choose their next action
 *  depending on the current state.
 */
abstract class Character(val id: EntityId) extends Entity with CharacterView {
  val position = cell(Pos(0, 0))
  val dimensions = cell((1, 1))
  
  def action(area: AreaView) = manager.action(area)
  
  def manager: Manager
}


object Character {
  def unapply(e: Entity): Option[EntityId] = if (e.isInstanceOf[Character]) Some(e.id) else None
}


object NoCharacter extends Character(invalidEntityId) {
  def manager = NoManager
  def pov(a: AreaView) = unsupported
}



/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine.model
package characters



import components._
import rules.{Stats, Inventory}



class Rock(val id: EntityId, sz: (Int, Int) = (3, 3)) extends Character {
  dimensions := sz
  
  def manager = IdleManager
  def canWalk(from: Slot, to: Slot) = false
  def chr = '#'
  def color = 0xffffff00
  def pov(area: AreaView) = area
}


package meadow {
  
  class Bush(val id: EntityId) extends Character {
    def manager = IdleManager
    def canWalk(from: Slot, to: Slot) = false
    def chr = 'T'
    def color = 0xffffff00
    def pov(area: AreaView) = area
  }
  
  class Shrub(val id: EntityId) extends Character {
    def manager = IdleManager
    def canWalk(from: Slot, to: Slot) = false
    def chr = 'T'
    def color = 0xffffff00
    def pov(area: AreaView) = area
  }
  
  class Pepperbush(val id: EntityId) extends Character {
    def manager = IdleManager
    def canWalk(from: Slot, to: Slot) = false
    def chr = 'T'
    def color = 0xffffff00
    def pov(area: AreaView) = area
  }
  
  class Forsythia(val id: EntityId) extends Character {
    def manager = IdleManager
    def canWalk(from: Slot, to: Slot) = false
    def chr = 'T'
    def color = 0xffffff00
    def pov(area: AreaView) = area
  }
  
  class Elderberry(val id: EntityId) extends Character {
    def manager = IdleManager
    def canWalk(from: Slot, to: Slot) = false
    def chr = 'T'
    def color = 0xffffff00
    def pov(area: AreaView) = area
  }
  
  class Sward(val id: EntityId) extends Character {
    def manager = IdleManager
    def canWalk(from: Slot, to: Slot) = false
    def chr = 'T'
    def color = 0xffffff00
    def pov(area: AreaView) = area
  }
  
}


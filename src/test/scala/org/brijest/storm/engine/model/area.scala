/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model



import org.scalatest._
import org.scalatest.matchers.ShouldMatchers



class AreaTests extends WordSpec with ShouldMatchers {
  
  "Area" should {
    
    "have a testing dummy" in {
      Area.testArea
    }
    
    "should be created" in {
      val area = new Area
    }
    
    "should change id" in {
      val area = new Area
      area.id() should equal (invalidAreaId)
      area.id := 5
      area.id() should equal (5)
    }
    
    "should change terrain" in {
      val area = new Area
      area.terrain(0, 0) should equal (HardRock0)
      area.terrain(0, 0) = DungeonFloor0
      area.terrain(0, 0) should equal (DungeonFloor0)
    }
    
    "should be assigned an item" in {
      val area = new Area
      val it = new Item.TestItem((0l, 0l))
      area.items.insertItem(0, 0, it)
      area.entities.next() should equal (it)
      area.items.locs(0, 0) should equal (it)
    }
    
  }
  
}

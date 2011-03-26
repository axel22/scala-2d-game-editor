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
      Area.simpleTestArea
    }
    
    "be created" in {
      val area = new Area
    }
    
    "change id" in {
      val area = new Area
      area.id() should equal (invalidAreaId)
      area.id := 5
      area.id() should equal (5)
    }
    
    "change terrain" in {
      val area = new Area
      area.terrain(0, 0) should equal (HardRock0)
      area.terrain(0, 0) = DungeonFloor0
      area.terrain(0, 0) should equal (DungeonFloor0)
    }
    
    "be assigned an item" in {
      val area = new Area
      val it = new Item.SimpleTestItem((0l, 0l))
      area.items.insert(0, 0, it)
      area.entities.next() should equal (it)
      area.items.locs(0, 0) should equal (it)
    }
    
    "have a test item" in {
      val area = Area.simpleTestArea
      area.entities.next()
    }
    
  }
  
  "Item table" should {
    
    "be inserted into" in {
      val tab = new ItemTable(1, 1)
      val it = Item.SimpleTestItem((0l, 0l))
      tab.insert(0, 0, it)
      tab.locs(0, 0) should equal (it)
    }
    
  }
  
  "Character table" should {
    
    "be inserted into" in {
      val tab = new CharacterTable(10, 10)
      val c = PlayerCharacter.simpleTestCharacter(PlayerId(0l))
      c.pos := Pos(5, 5)
      tab.insert(c)
      tab.locs(4, 4) should equal (NoCharacter)
      tab.locs(5, 5) should equal (c)
      tab.ids(c.id) should equal (c)
    }
    
  }
  
}
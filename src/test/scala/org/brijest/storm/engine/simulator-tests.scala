/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine



import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import model._



class SimulatorTests extends WordSpec with ShouldMatchers {
  
  "Simulator" should {
    
    "simulate when there are no events" in {
      val s = new Simulator(Area.emptyArea)
      s.step()._1 should equal (0)
      s.step()._2 should equal (Nil)
      s.time should equal (2)
    }
    
    "simulate a test item" in {
      val s = new Simulator(Area.simpleTestArea)
      val (acn, acts) = s.step()
      
      acn should equal (0)
      acts.length should equal (1)
      acts(0) should equal (NoAction)
      s.time should equal (1)
      s.hasNextEvent should equal (true)
      s.nextEventAt should equal (1)
      
      val (acn2, acts2) = s.step()
      
      acn2 should equal (1)
      acts.length should equal (1)
      acts(0) should equal (NoAction)
    }
    
    "simulate a test item for a long time" in {
      val s = new Simulator(Area.simpleTestArea)
      for (i <- 1 until 100) {
        val (acn, acts) = s.step()
        s.time should equal (i)
        acts.length should equal (1)
        acts(0) should equal (NoAction)
      }
    }
    
  }
  
}

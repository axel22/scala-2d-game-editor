/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.gui



import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import iso._



class MemoryPoolTests extends WordSpec with ShouldMatchers {
    
  "Memory pool" should {
    
    "create and dispose objects" in {
      class Next extends Linked[Next] { def reset() {} }
      val m = new MemoryPool(new Next)
      
      val a = m.create
      val b = m.create
      val c = m.create
      val d = m.create
      d.next = c
      
      m.dispose(a)
      m.dispose(b)
      m.dispose(d)
      
      for (i <- 0 until 4) m.create.next should equal (null)
    }
    
    "create and dispose many objects" in {
      class Next extends Linked[Next] { def reset() {} }
      val m = new MemoryPool(new Next)
      val sz = 5000
      
      val objs = for (i <- 0 until sz) yield m.create
      for (o <- objs) m.dispose(o)
      for (i <- 0 until sz) m.create
    }
    
  }
  
}

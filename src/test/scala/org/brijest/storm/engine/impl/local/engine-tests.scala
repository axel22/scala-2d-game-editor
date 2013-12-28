package org.brijest.storm.engine
package impl.local



import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import model._
import org.brijest.storm.Config



class EngineTests extends WordSpec with ShouldMatchers {
  
  "LocalEngine" should {
    
    "be ended through a script" in {
      val eng = new LocalEngine(new Config, Player.default(PlayerId(0L)), new World.TestWorld)
      eng.start()
      eng.script("end()")
      eng.awaitTermination()
    }
    
    "be paused and resumed through a script" in {
      val eng = new LocalEngine(new Config, Player.default(PlayerId(0L)), new World.TestWorld)
      eng.start()
      eng.script("pause()")
      assert(eng.isPaused)
      eng.script("resume()")
      eng.script("end()")
      eng.awaitTermination()
    }
    
    "be toggle the pause state through a script" in {
      val eng = new LocalEngine(new Config, Player.default(PlayerId(0L)), new World.TestWorld)
      eng.start()
      eng.script("togglePause()")
      assert(eng.isPaused)
      eng.script("togglePause()")
      eng.script("end()")
      eng.awaitTermination()
    }
    
  }
  
}

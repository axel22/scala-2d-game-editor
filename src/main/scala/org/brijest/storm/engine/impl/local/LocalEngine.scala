/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package impl.local



import scala.util.parsing.combinator._
import scala.concurrent.SyncVar
import collection._
import model._



class LocalEngine(config: Config, val player: Player, w: World) extends Engine {
engine =>
  private val playeruis = mutable.ArrayBuffer[UI]()
  @volatile private var running = true
  @volatile private var paused = false
  
  def isPaused = paused
  
  /* simulation thread */
  
  class SimulationThread extends Thread("Local simulator") {
    val area = w.initializeArea(w.initialPosition(player))
    val sim = new Simulator(area)
    
    override def run() {
      val pc = w.initialPlace(player, area)
      sim.init()
      playeruis.foreach(_.refresh(area))
      
      while (running) {
        /* collect inputs */
        for (ui <- playeruis; comm <- ui.flushCommands()) comm match {
          case OrderCommand(plid, o) => pc.order := o
        }
        
        /* step through simulation */
        val (_, actions) = sim.step()
        
        /* update screens */
        playeruis.foreach(_.update(actions, area))
        
        /* wait */
        Thread.sleep(10)
        engine.synchronized {
          while (paused) engine.wait()
        }
      }
    }
  }
  
  val simthr = new SimulationThread
  
  def start() = simthr.start()
  
  def awaitTermination() = simthr.join()
  
  def listen(ui: UI) = {
    playeruis += ui
    ui.playerId = player.id
  }
  
  /* scripting */
  
  object dsl extends syntactical.StandardTokenParsers {
    val global = mutable.HashMap[String, List[Any] => Any](
      "end" -> {
        xs => engine.synchronized {
          paused = false
          running = false
          engine.notify()
        }
      },
      "pause" -> {
        xs => engine.synchronized {
          if (running) {
            paused = true
            engine.notify()
          }
        }
      },
      "resume" -> {
        xs => engine.synchronized {
          paused = false
          engine.notify()
        }
      }
    )
    
    def interpret(m: String) = script(new lexical.Scanner(m)) match {
      case Success(obj, _) => obj
      case Failure(msg, _) => /*sys.*/error(msg)
      case Error(msg, _) => /*sys.*/error(msg)
    }
    
    /* language */
    
    lexical.delimiters ++= List("(", ")", ",")
    
    def script: Parser[Any] = expression
    def expression: Parser[Any] = functioncall
    def functioncall: Parser[Any] = ident ~ argslist ^^ {
      case func ~ args => global(func)(args)
    }
    def argslist = "(" ~> repsep(expression, ",") <~ ")" ^^ {
      case os: List[Any] => os
    }
  }
  
  def script(m: String) = dsl.interpret(m)
}




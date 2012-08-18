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
import com.weiglewilczek.slf4s._
import model._



class LocalEngine(config: Config, val player: Player, w: World) extends Engine with Engine.State with Logging {
engine =>
  private val playeruis = mutable.ArrayBuffer[UI]()
  private val commands = mutable.ArrayBuffer[Command]()
  @volatile private var running = true
  @volatile private var paused = false
  
  def isPaused = paused
  
  /* simulation thread */
  
  class SimulationThread extends Thread("Local simulator") {
    val area = w.area(w.position(player)).get.acquire()
    val sim = new Simulator(area)
    val pc = w.pc(player)
    
    override def run() {
      sim.init()
      
      while (running) {
        /* collect inputs */
        processCommands()
        
        /* step through simulation */
        val (_, actions) = sim.step()
        
        /* wait */
        Thread.sleep(10)
        engine.synchronized {
          while (paused) {
            processCommands()
            if (paused) engine.wait()
          }
        }
      }
    }
    
    private def processCommands() {
      engine.synchronized {
        for (comm <- commands) comm match {
          case OrderCommand(plid, o) => pc.order.:=(o)(area)
          case ScriptCommand(s) => script(s)
          case EmptyCommand => // do nothing
        }
        commands.clear()
      }
    }
  }
  
  val simthr = new SimulationThread
  
  def start() = simthr.start()
  
  def awaitTermination() = simthr.join()
  
  def listen(ui: UI) = {
    playeruis += ui
  }
  
  def push(comm: Command) = engine.synchronized {
    commands += comm
    engine.notify()
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
      },
      "togglePause" -> {
        xs => engine.synchronized {
          if (running) {
            paused = !paused
            engine.notify()
          }
        }
      }
    )
    
    def interpret(m: String) = script(new lexical.Scanner(m)) match {
      case Success(obj, _) => obj
      case Failure(msg, _) => sys.error(msg)
      case Error(msg, _) => sys.error(msg)
    }
    
    /* language */
    
    lexical.delimiters ++= List("(", ")", ",")
    
    def script: Parser[Any] = expression
    def expression: Parser[Any] = functioncall
    def functioncall: Parser[Any] = ident ~ argslist ^^ {
      case func ~ args => global(func)(args)
    }
    def argslist = "(" ~> repsep(expression, ",") <~ ")" ^^ {
      case os: List[_] => os
    }
  }
  
  def script(m: String) = dsl.interpret(m)
}




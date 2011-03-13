/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import org.brijest.bufferz.shells._
import org.triggerspace._
import engine._
import engine.impl._
import engine.model.World



object Initializer {
  
  import Config._
  
  def apply(config: Config): Clients = {
    // setup debug info
    config.logging match {
      case Some(logging.screen) =>
        import java.util.logging._
        LogManager.getLogManager.readConfiguration(javaLoggingScreen)
      case _ => 
    }
    
    // setup engine
    config.engine match {
      case engine.local =>
        val clients = new local.LocalClients(config, createWorld(config), new threadlocking2.LockingTransactors {})
        clients.delegateUI = createUI(config)
        clients
      case e => exit("Engine '%s' not recognized.".format(e))
    }
  }
  
  private def createUI(config: Config): UI = config.ui match {
    case ui.swingConsole =>
      val ui = new ConsoleUI
      ui.delegateShell = new SwingShell(app.name)
      ui
    case e => exit("User interface '%s' not recognized".format(e))
  }
  
  private def createWorld(config: Config): World = config.world match {
    case None => new World.DefaultWorld
    case Some(_) => exit("Arbitrary worlds not yet supported.")
  }
  
  private def javaLoggingScreen = {
    import java.io._
    val conf =
"""
handlers=java.util.logging.ConsoleHandler
.level=INFO
java.util.logging.ConsoleHandler.level=INFO
"""
    new ByteArrayInputStream(conf.getBytes("UTF-8"))
  }
  
}

package org.brijest.storm



import org.brijest.bufferz.shells._

import engine._
import engine.impl._
import engine.model.World


object Initializer {
  
  def apply(config: Config): Clients = config.engine match {
    case Config.engine.local =>
      val clients = new local.LocalClients(config, createWorld(config))
      clients.delegateUI = createUI(config)
      clients
    case e => exit("Engine '%s' not recognized.".format(e))
  }
  
  private def createUI(config: Config): UI = config.ui match {
    case Config.ui.swingConsole =>
      val ui = new ConsoleUI
      ui.delegateShell = new SwingShell(app.name)
      ui
    case e => exit("User interface '%s' not recognized".format(e))
  }
  
  private def createWorld(config: Config): World = config.world match {
    case None => new World.DefaultWorld
    case Some(_) => exit("Arbitrary worlds not yet supported.")
  }
  
}

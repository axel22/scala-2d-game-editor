/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm






final class Config {
  var ui: String = _
  var engine: String = _
  var savename: String = _
  var logging: Option[String] = None
  var world: Option[String] = None
  val basedir = System.getProperty("user.dir")
  val savedir = "saves"
}


object Config {
  
  object logging {
    val screen = "screen"
  }
  
  val loggings = List(
    logging.screen
  )
  
  object ui {
    val swingConsole = "swing-console"
    val swingSprites = "swing-sprites"
  }
  
  val uis = List(
    ui.swingConsole,
    ui.swingSprites
  )
  
  object engine {
    val local = "local"
  }
  
  val engines = List(
    engine.local
  )
  
}

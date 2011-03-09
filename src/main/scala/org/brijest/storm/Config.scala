package org.brijest.storm






final class Config {
  var ui: String = _
  var engine: String = _
  var savename: String = _
  var logging: Option[String] = None
  var world: Option[String] = None
  val basedir = System.getProperty("user.dir")
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
  }
  
  val uis = List(
    ui.swingConsole
  )
  
  object engine {
    val local = "local"
  }
  
  val engines = List(
    engine.local
  )
  
}

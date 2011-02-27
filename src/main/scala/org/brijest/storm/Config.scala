package org.brijest.storm






final class Config {
  var ui: String = _
  var engine: String = _
  var world: Option[String] = None
}


object Config {
  
  val uis = List(
    "swing-console"
  )
  
  val engines = List(
    "local"
  )
  
}

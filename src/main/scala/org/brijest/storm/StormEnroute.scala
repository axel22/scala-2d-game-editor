package org.brijest.storm



import org.github.scopt._



object StormEnroute {
  
  def main(args: Array[String]) {
    val config = new Config
    val parser = new ConfigParser(config)
    
    if (parser.parse(args)) {
      Initializer(config)
    }
  }
  
}


class ConfigParser(config: Config) extends OptionParser(app.command) {
  opt("w", "world", "The specific world to simulate.", { v: String => config.world = Some(v) })
  help("h", "help", "Show this help message.")
  arg("<engine>", "The simulation engine, one of: " + Config.engines.mkString(", "), { v: String => config.engine = v })
  arg("<ui>", "The user interface, one of: " + Config.uis.mkString(", "), { v: String => config.ui = v })
  arg("<savename>", "The name of the save file, creates a new one if it doesn't exist.", { v: String => config.savename = v})
}



/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import org.github.scopt._
import engine.model._



object Editor {
  
  def main(args: Array[String]) {
    val config = new Config
    val parser = new EditorConfigParser(config)
    
    if (parser.parse(args)) {
      startEditor(config)
    }
  }
  
  def startEditor(config: Config) {
    new SwingEditor(config)
  }
  
}


class EditorConfigParser(config: Config) extends OptionParser(app.command) {
  help("h", "help", "Show this help message")
  opt("width", "The width for the area, if it's newly created", { v: String => config.area.width = v.toInt })
  opt("height", "The height for the area, if it's newly created", { v: String => config.area.height = v.toInt })
  arg("<areaname>", "The name of the area, creates one if it doesn't exist.", { v: String => config.area.name = v})
}


class SwingEditor(config: Config) extends engine.gui.iso.SwingIsoUI(app.editorname) {
  val area = Area.emptyDungeon(config.area.width, config.area.height)
  
  engine = Some(org.brijest.storm.engine.IdleEngine)
  refresh(area, engine.get)
}














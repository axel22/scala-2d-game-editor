/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import org.github.scopt._
import scala.swing._
import java.awt.image._
import engine.model._
import engine.gui.iso._



object Editor {
  
  def main(args: Array[String]) {
    val config = new Config
    val parser = new EditorConfigParser(config)
    
    Initializer.default()
    
    if (parser.parse(args)) {
      startEditor(config)
    }
  }
  
  def startEditor(config: Config) {
    new Editor(config)
  }
  
}


class EditorConfigParser(config: Config) extends DefaultParser(app.command) {
  help("h", "help", "Show this help message")
  opt("width", "The width for the area, if it's newly created", { v: String => config.area.width = v.toInt })
  opt("height", "The height for the area, if it's newly created", { v: String => config.area.height = v.toInt })
  arg("<areaname>", "The name of the area, creates one if it doesn't exist.", { v: String => config.area.name = v})
}


class Editor(config: Config)
extends GLIsoUI(app.editorname) {
  val area = Area.emptyDungeonTest4(config.area.width, config.area.height)
  val refresher = new Thread {
    override def run() = while (true) {
      areadisplay.repaint()
      Thread.sleep(36)
    }
  }
  
  refresher.start()
  engine = Some(org.brijest.storm.engine.IdleEngine)
  refresh(area, engine.get)
  
  /* events */
  
  var lastpress = (0, 0);
  var mode = 'none
  
  def onMiddleDrag(p: (Int, Int)) {
    pos = ((pos._1 + lastpress._1 - p._1).toInt, (pos._2 + lastpress._2 - p._2).toInt);
    lastpress = p
  }
  
  def onMiddlePress(p: (Int, Int)) {
    lastpress = p
    mode = 'drag
  }
  
  def onMiddleRelease(p: (Int, Int)) {
    mode = 'none
  }
  
  /* awt events */
  
  import java.awt.event._
  
  areadisplay.addMouseListener(new MouseAdapter {
    override def mousePressed(me: MouseEvent) {
      if (me.getButton == MouseEvent.BUTTON2) onMiddlePress((me.getX, me.getY))
    }
    override def mouseReleased(me: MouseEvent) {
      if (me.getButton == MouseEvent.BUTTON2) onMiddleRelease((me.getX, me.getY))
    }
  })
  
  areadisplay.addMouseMotionListener(new MouseMotionAdapter {
    override def mouseDragged(me: MouseEvent) {
      if (mode == 'drag) onMiddleDrag((me.getX, me.getY))
    }
  })
  
  /* former event handlers for the swing ui */
  
  // areadisplay.listenTo(areadisplay.mouse.clicks)
  // areadisplay.listenTo(areadisplay.mouse.moves)
  
  // areadisplay.reactions += {
  //   case e @ event.MousePressed(_, p, mods, clicks, trig) =>
  //     if (e.peer.getButton == java.awt.event.MouseEvent.BUTTON1) lastpress = p
  //   case event.MouseDragged(_, p, mods) =>
  //     pos = ((pos._1 + lastpress.getX - p.getX).toInt, (pos._2 + lastpress.getY - p.getY).toInt);
  //     lastpress = p
  //     refresh(area, engine.get)
  // }
  
  // areadisplay.listenTo(areadisplay)
  
  // areadisplay.reactions += {
  //   case event.UIElementResized(_) => this.synchronized {
  //     buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
  //     refresh(area, engine.get)
  //   }
  // }
  
}














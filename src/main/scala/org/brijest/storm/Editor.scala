/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import org.github.scopt._
import org.eclipse.swt._
import org.eclipse.swt.widgets._
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


class EditorConfigParser(config: Config) extends DefaultParser(app.editorcommand) {
  help("h", "help", "Show this help message")
  opt("width", "The width for the area, if it's newly created", { v: String => config.area.width = v.toInt })
  opt("height", "The height for the area, if it's newly created", { v: String => config.area.height = v.toInt })
  arg("<areaname>", "The name of the area, creates one if it doesn't exist.", { v: String => config.area.name = v})
}


class Editor(config: Config) extends AnyRef with GLIsoUI {
  val area = Area.tileTest(config.area.width, config.area.height)
  val refresher = new Thread {
    setDaemon(true)
    override def run() = while (true) {
      areadisplay.repaint()
      Thread.sleep(20)
    }
  }
  
  refresher.start()
  engine = Some(org.brijest.storm.engine.IdleEngine)
  refresh(area, engine.get)
  
  val dispatchThread = new Thread() {
    setDaemon(true)
    override def run() {
      val display = Display.getDefault()
      val editorwindow = new editor.EditorWindow(display)
      editorwindow.areaCanvasPane.add(areadisplay)
      editorwindow.setVisible(true)
      
      for (cls <- Terrain.registered) {
        val inst = cls.newInstance
        val tableItem = new TableItem(editorwindow.terrainTable, SWT.NONE);
        val image = new graphics.Image(display, pngStream(inst.identifier));
        tableItem.setImage(0, image)
        tableItem.setText(1, cls.getSimpleName);
        tableItem.setText(2, inst.identifier);
      }
      
      editorwindow.open()
      editorwindow.layout()
      while (!editorwindow.isDisposed()) {
	if (!display.readAndDispatch()) {
	  display.sleep()
	}
      }
    }
  }
  dispatchThread.start()
  
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
  
}














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
import org.eclipse.swt.custom._
import org.eclipse.swt.widgets._
import org.eclipse.swt.events._
import java.awt.image._
import javax.media.opengl._
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


class Editor(config: Config) extends Logging {
  // val area = Area.tileTest(config.area.width, config.area.height)
  // val refresher = new Thread {
  //   setDaemon(true)
  //   override def run() = while (true) {
  //     areadisplay.repaint()
  //     Thread.sleep(20)
  //   }
  // }
  
  // refresher.start()
  // engine = Some(org.brijest.storm.engine.IdleEngine)
  // refresh(area, engine.get)
  
  val glp = GLProfile.getDefault()
  val caps = new GLCapabilities(glp)
  
  def createGLIsoUI(area: Area) = {
    val areadisplay = new GLIsoUI(area, caps)
    
    /* events */
    
    var lastpress = (0, 0);
    var mode = 'none
    
    def onMiddleDrag(p: (Int, Int)) {
      areadisplay.pos = ((areadisplay.pos._1 + lastpress._1 - p._1).toInt, (areadisplay.pos._2 + lastpress._2 - p._2).toInt);
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
        areadisplay.repaint()
      }
    })
    
    areadisplay
  }
  
  val world = config.world match {
    case Some(name) => sys.error("unsupported")
    case None => new World.Default("Untitled")
  }
  
  val dispatchThread = new Thread() {
    override def run() {
      val display = Display.getDefault()
      val editorwindow = new editor.EditorWindow(display)
      editorwindow.setVisible(true)
      editorwindow.setImage(new graphics.Image(display, pngStream("lightning")))
      
      import editorwindow._
      
      def loadPlaneTable() {
        planeTable.removeAll()
        for ((id, plane) <- world.planes.toSeq.sortBy(_._1)) {
          val item = new TableItem(planeTable, SWT.NONE)
          item.setText(0, id.toString)
          item.setText(1, plane.name)
          item.setText(2, plane.details)
        }
      }
      
      def loadWorldInfo() {
        worldNameLabel.setText(world.name)
        mainPlaneCombo.removeAll()
        
        val sortedplanes = world.planes.toSeq.sortBy(_._1)
        for ((id, plane) <- sortedplanes) {
          mainPlaneCombo.add(id + ": " + plane.name)
        }
        mainPlaneCombo.select(sortedplanes.indexWhere(_._1 == world.mainPlane))
        
        totalPlanesLabel.setText(sortedplanes.size.toString)
      }
      
      /* initialize */
      
      for (cls <- Terrain.registered) {
        val inst = cls.newInstance
        val tableItem = new TableItem(terrainTable, SWT.NONE);
        val image = new graphics.Image(display, pngStream(inst.identifier));
        tableItem.setImage(0, image)
        tableItem.setText(1, cls.getSimpleName);
        tableItem.setText(2, inst.identifier);
      }
      
      openAreaMenuItem.addSelectionListener(new SelectionAdapter() {
	override def widgetSelected(e: SelectionEvent) {
          val selection = planeTable.getSelection
          if (selection.nonEmpty) {
            val id = selection.head.getText(0).toInt
            val chooser = new editor.PlaneAreaChooser(editorwindow, SWT.NONE)
            val coord = chooser.open().asInstanceOf[graphics.Point]
            val (x, y) = (coord.x, coord.y);
            val areaid = areaId(id, x, y)
            val area = world.area(areaid) match {
              case Some(a) => a
              case None =>
                val a = Area.tileTest(config.area.width, config.area.height)
                // TODO add to world
                a
            }
            
            val tbtmMap = new CTabItem(leftTabs, SWT.CLOSE);
            tbtmMap.setText("Area: " + world.plane(id).get.name + " at (" + x + ", " + y + ")");
            
            areaPanel = new editor.AreaPanel(leftTabs, SWT.NONE)
            tbtmMap.setControl(areaPanel)
            
            val canvasPane = createGLIsoUI(area)
            
            areaPanel.areaCanvasPane.add(canvasPane)
          }
	}
      })
      
      loadPlaneTable()
      loadWorldInfo()
      
      try {
        open()
        layout()
        while (!isDisposed()) {
	  if (!display.readAndDispatch()) {
	    display.sleep()
          }
        }
      } catch {
        case e => logger.error("exception in event dispatch thread: " + e)
      }
    }
  }
  dispatchThread.start()
  
}














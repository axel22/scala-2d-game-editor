/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import collection._
import swing._
import java.awt.image._
import java.lang.ref.SoftReference
import org.brijest.storm.engine.model._



class SwingIsoUI(val name: String) extends IsoUI {
  
  val stars = javax.imageio.ImageIO.read(getClass.getResourceAsStream("/stars.png"))
  val buffer = new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR)
  val underlying = new Component {
    override def paintComponent(g: Graphics2D) {
      super.paintComponent(g)
      g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                         java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
      buffer.synchronized {
        g.drawImage(stars, 0, 0, 800, 600, 0, 0, 800, 600, null, null)
        g.drawImage(buffer, 0, 0, 640, 480, 0, 0, 640, 480, null, null)
      }
    }
  }
  
  val frame = new Frame {
    title = name
    contents = underlying
    underlying.requestFocus()
  }
    
  frame.size = new Dimension(640, 480)
  frame.open()
  
  val refresher = new Thread {
    override def run() = while (true) {
      underlying.repaint()
      Thread.sleep(36)
    }
  }
  
  refresher.start()
  
  /* implementations */
  
  def width: Int = frame.size.width
  
  def height: Int = frame.size.height
  
  def refresh(area: AreaView, state: Engine.State) = buffer.synchronized {
    redraw(area, state, new SwingDrawingAdapter)
  }
  
  class SwingDrawingAdapter extends DrawingAdapter {
  }
  
}






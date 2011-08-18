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
  
  var buffer = new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR)
  val areadisplay = new AreaDisplay
  
  class AreaDisplay extends Component {
    override def paintComponent(g: Graphics2D) {
      super.paintComponent(g)
      g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                         java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
      
      this.synchronized {
        g.drawImage(buffer, 0, 0, width, height, 0, 0, width, height, null, null)
      }
    }
  }
  
  val frame = new Frame {
    title = name
    contents = areadisplay
    areadisplay.requestFocus()
  }
  
  frame.size = new Dimension(640, 480)
  frame.peer.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  frame.open()
  
  /* implementations */
  
  def width: Int = frame.size.width
  
  def height: Int = frame.size.height
  
  def refresh(area: AreaView, state: Engine.State) = this.synchronized {
    val sad = new SwingDrawAdapter
    
    val t = timed {
      redraw(area, state, sad)
    }
    
    //println("Time to render: %d ms".format(t))
  }
  
  def characterSprite(c: CharacterView) = new Sprite { def height = 0 } // TODO
  
  def maxSpriteHeight = 320
  
  type Img = java.awt.Image
  
  def imageFromPngStream(stream: java.io.InputStream) = javax.imageio.ImageIO.read(stream)
  
  class SwingDrawAdapter extends DrawAdapter {
    val gr = buffer.getGraphics.asInstanceOf[Graphics2D]
    
    def drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
      gr.drawLine(x1, y1, x2, y2)
    }
    def setColor(r: Int, g: Int, b: Int) {
      gr.setColor(new java.awt.Color(r, g, b))
    }
    def drawString(s: String, x: Int, y: Int) {
      gr.drawString(s, x, y)
    }
    def setFontSize(sz: Float) {
      gr.setFont(gr.getFont.deriveFont(sz))
    }
    def drawPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int) {
      gr.drawPolyline(xpoints, ypoints, n)
    }
    def fillPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int) {
      gr.fillPolygon(xpoints, ypoints, n)
    }
    def drawImage(image: Img, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int) {
      gr.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, null)
    }

  }
  
}






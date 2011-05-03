/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package impl



import swing._
import java.awt.image._
import org.brijest.storm.engine.model._



class SwingSpriteUI(val name: String) extends SpriteUI {
  
  val underlying = new Component {
    override def paintComponent(g: Graphics2D) {
      super.paintComponent(g)
      g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                         java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
      g.drawImage(buffer, 0, 0, 640, 480, 0, 0, 640, 480, null, null)
    }
  }
  
  val frame = new Frame {
    title = name
    contents = underlying
    underlying.requestFocus()
  }
    
  frame.size = new Dimension(640, 480)
  frame.open()
  
  val buffer = new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR) 
  
  def sprite(e: EntityView): Sprite = NullSprite
  
  def sprite(t: Slot): Sprite = {
    val stream = SpriteUI.streamFor(t)
    val img = javax.imageio.ImageIO.read(stream)
    new SwingSprite(img)
  }
  
  def width: Int = frame.size.width
  
  def height: Int = frame.size.height
  
  class SwingSprite(image: Image) extends Sprite {
    def draw(x: Int, y: Int, frame: Int) {
      buffer.createGraphics.drawImage(image, x, y, x + swdt, y + shgt, 0, 0, 32, 32, null, null)
    }
  }
  
}







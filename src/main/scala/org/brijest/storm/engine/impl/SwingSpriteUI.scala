/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package impl



import collection._
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
    
  val buffer = new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR)
  
  frame.size = new Dimension(640, 480)
  frame.open()
  
  /* implementations */
  
  class SwingSprite(val image: Image, val wdt: Int, val hgt: Int, val xoffset: Int, val yoffset: Int) extends Sprite {
    def draw(x: Int, y: Int, frame: Int) {
      buffer.createGraphics.drawImage(
        image, 
        x + xoffset, 
        y + yoffset, 
        x + xoffset + wdt, 
        y + yoffset + hgt, 
        0, 0, wdt, hgt, null, null)
    }
  }
  
  def width: Int = frame.size.width
  
  def height: Int = frame.size.height
  
  import java.lang.ref.SoftReference
  
  object palette extends Palette {
    val imageinfos = mutable.HashMap[String, ImageInfo]()
    val images = mutable.HashMap[String, SoftReference[Image]]()
    val spritez = mutable.HashMap[Int, SoftReference[Sprite]]()
    
    private def loadImage(group: String) = {
      val stream = SpriteUI.pngStream(group)
      try {
        val img = javax.imageio.ImageIO.read(stream) 
        images.put(group, new SoftReference(img))
        img
      } finally {
        stream.close()
      }
    }
    
    private def imageinfo(group: String) = imageinfos.get(group) match {
      case Some(i) => i
      case None =>
        val nfo = SpriteUI.imageInfo(group)
        imageinfos.put(group, nfo)
        nfo
    }
    
    private def image(group: String) = images.get(group) match {
      case Some(tref) =>
        val ts = tref.get
        if (ts eq null) loadImage(group) else ts
      case None => loadImage(group)
    }
    
    private def loadSprite(id: Int): Sprite = {
      val (group, name) = Slot.idents(id)
      val ts = image(group)
      val info = imageinfo(group)
      val ((x, y), (w, h), (xoff, yoff)) = info(id)
      val wdt = w * swdt
      val hgt = h * shgt
      val img = new BufferedImage(wdt, hgt, BufferedImage.TYPE_4BYTE_ABGR)
      img.createGraphics.drawImage(ts, 0, 0, wdt, hgt, x * swdt, y * swdt, x * swdt + wdt, y * hgt + hgt, null, null)
      val sprite = new SwingSprite(img, wdt, hgt, xoff * swdt, yoff * shgt)
      spritez.put(id, new SoftReference(sprite))
      sprite
    }
    
    def sprite(t: Slot): Sprite = spritez.get(t.identifier) match {
      case Some(sref) =>
        val s = sref.get
        if (s eq null) loadSprite(t.identifier) else s
      case None => loadSprite(t.identifier)
    }
    def sprite(e: EntityView): Sprite = NullSprite
  }
  
}






/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.sprite



import collection._
import swing._
import java.awt.image._
import java.lang.ref.SoftReference
import org.brijest.storm.engine.model._



@deprecated
class SwingSpriteUI(val name: String) extends SpriteUI {
  
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
    
  val stars = javax.imageio.ImageIO.read(getClass.getResourceAsStream("/stars.png"))
  val buffer = new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR)
  
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
  
  class SwingSprite(val image: Image, val wdt: Int, val hgt: Int, val xoffset: Int, val yoffset: Int)
  extends Sprite {
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
  
  override def refresh(area: AreaView, state: Engine.State) = buffer.synchronized {
    super.refresh(area, state)
  }
  
  object palette extends Palette {
    val imageinfos = mutable.HashMap[String, SpriteUI.ImageInfo]()
    val images = mutable.HashMap[String, SoftReference[Image]]()
    val spritez = mutable.HashMap[String, SoftReference[Sprite]]()
    
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
    
    private def loadSprite(id: String): Sprite = {
      val s = id.split("\\.")
      val (group, name) = (s(0), s(1))
      val ts = image(group)
      val info = imageinfo(group)
      val ((x, y), (w, h), (xoff, yoff)) = info(id, 0)
      val wdt = w
      val hgt = h
      val img = new BufferedImage(wdt, hgt, BufferedImage.TYPE_4BYTE_ABGR)
      img.createGraphics.drawImage(ts, 0, 0, wdt, hgt, x, y, x + wdt, y + hgt, null, null)
      val sprite = new SwingSprite(img, wdt, hgt, xoff, yoff)
      spritez.put(id, new SoftReference(sprite))
      sprite
    }
    
    def sprite(ident: String): Sprite = spritez.get(ident) match {
      case Some(sref) =>
        val s = sref.get
        if (s eq null) loadSprite(ident) else s
      case None => loadSprite(ident)
    }
    
    def sprite(e: EntityView): Sprite = sprite(e.identifier)
    
    def sprite(s: Slot): Sprite = sprite(s.identifier)
  }
  
}






/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import org.apache.commons.io.IOUtils
import java.awt.{Image => JImage}
import org.brijest.storm.util._
import collection._
import model._



trait Palette[Image] {
  
  trait Sprite {
    def img: Image = image(0)
    def image(frame: Int): Image
    def width: Int
    def height: Int
    def xoffset: Int
    def yoffset: Int
    def frames: Int
    def animated: Boolean
  }
  
  def sprite(c: Character): Sprite
  
  def sprite(e: Effect): Sprite
  
  def sprite(t: Slot): Sprite
  
  def wall(t: Slot): Sprite
  
  def maxSpriteHeight: Int
  
}


trait Parsing[Image] extends Palette[Image] {
  import scala.util.parsing.combinator._
  
  trait Sprite extends super.Sprite {
    var width = 48
    var height = 30
    var xoffset = 24
    var yoffset = 15
    var animated = false
    var frames = 0
    
    def set(kv: (String, String)) = kv match {
      case ("width", w) => width = w.toInt
      case ("height", h) => height = h.toInt
      case ("xoffset", xo) => xoffset = xo.toInt
      case ("yoffset", yo) => yoffset = yo.toInt
      case ("animated", ani) => animated = ani.toBoolean
      case ("frames", f) => frames = f.toInt
    }
    
    override def toString = "Sprite(%d, %d, %d, %d: x%d)".format(width, height, xoffset, yoffset, frames)
  }
  
  def parseSpriteInfo(s: Sprite, text: String) = {
    class SpriteInfoParser extends syntactical.StandardTokenParsers {
      lexical.delimiters ++= List("{", "}", ",", ":", "=", ";", ".", "-")
      
      def parse() = entry(new lexical.Scanner(text)) match {
        case Success(obj, _) => obj
        case Failure(msg, _) => sys.error("Failure: " + msg)
        case Error(msg, _) => sys.error("Error: " + msg)
      }
      
      def entry: Parser[Unit] = "{" ~ properties ~ "}" ^^ {
        case _ ~ sprite ~ _ =>
      }
      def properties: Parser[Unit] = rep(prop) ^^ {
        case props => for (p <- props) s.set(p)
      }
      def prop: Parser[(String, String)] = ident ~ "=" ~ value ^^ {
        case nm ~ _ ~ v => (nm, v)
      }
      def value: Parser[String] = numericLit | stringLit
    }
    
    new SpriteInfoParser parse()
  }
  
}


trait Images[Image] extends Palette[Image] {
  def newImage(name: String): Image
}


trait Caching[Image] extends Palette[Image] {
  import java.lang.ref.SoftReference
  
  type Cachee >: Null <: AnyRef
  
  val activesprites = new CircularQueue[Cachee](512)
  val spritemap = mutable.Map[String, SoftReference[Cachee]]()
  
  def addCache(name: String, c: Cachee) {
    if (!spritemap.contains(name) || spritemap(name).get == null) {
      activesprites += c
      spritemap(name) = new SoftReference(c)
    }
  }
  
  def getCache(name: String): Cachee = if (spritemap.contains(name)) spritemap(name).get else null
  
}


class DefaultSwingPalette extends Palette[JImage] with Parsing[JImage] with Images[JImage] with Caching[JImage] {
  
  /* types */
  
  type Img = java.awt.image.BufferedImage
  
  def newImage(name: String) = javax.imageio.ImageIO.read(pngStream(name))
  
  class Sprite(val images: Seq[Img]) extends super[Parsing].Sprite {
    def image(frame: Int) = images(frame)
  }

  object NullSprite extends Sprite(null)
  
  def newSprite(imgs: Seq[Img]) = new Sprite(imgs)
  
  type Cachee = Sprite
  
  /* internal */
  
  def loadSprite(name: String) = {
    val pngimage = new com.sixlegs.png.AnimatedPngImage()
    val tmpfile = java.io.File.createTempFile("storm", "tmp")
    tmpfile.deleteOnExit()
    val pngis = pngStream(name)
    val tmpfos = new java.io.FileOutputStream(tmpfile)
    try {
      IOUtils.copy(pngis, tmpfos)
    } finally {
      pngis.close()
      tmpfos.close()
    }
    val images = pngimage.readAllFrames(tmpfile)
    val s = newSprite(images)
    val dschunk = pngimage.getTextChunk("descriptor")
    if (dschunk != null) parseSpriteInfo(s, dschunk.getText)
    
    // set known
    s.width = s.img.getWidth(null)
    s.height = s.img.getHeight(null)
    s.frames = s.images.length
    
    s
  }
  
  def sprite(name: String): Sprite = {
    val cached = getCache(name)
    if (cached != null) cached else {
      val s = loadSprite(name)
      addCache(name, s)
      s
    }
  }
  
  /* api */
  
  def sprite(c: Character): Sprite = c match {
    case NoCharacter => NullSprite
    case c => sprite(c.identifier)
  }
  
  def sprite(e: Effect) = null
  
  def sprite(t: Slot) = sprite(t.identifier)
  
  def wall(t: Slot) = sprite(t.identifier + "-wall")
  
  def maxSpriteHeight = Sprites.maxheight
  
}




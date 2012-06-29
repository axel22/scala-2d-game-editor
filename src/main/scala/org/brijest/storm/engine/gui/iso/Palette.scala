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
import java.awt.image.{BufferedImage => JBImage}
import org.brijest.storm.util._
import collection._
import model._



trait Palette[Image] {
  
  trait BaseSprite {
    def img: Image = image(0)
    def image(frame: Int): Image
    def width: Int
    def height: Int
    def xoffset: Int
    def yoffset: Int
    def frames: Int
    def animated: Boolean
  }
  
  type Sprite >: Null <: BaseSprite
  
  def sprite(c: Character): Sprite
  
  def sprite(e: Effect): Sprite
  
  def sprite(t: Slot): Sprite
  
  def wall(t: Slot): Sprite
  
  def maxSpriteHeight: Int
  
  def newImageFromPngStream(stream: java.io.InputStream): Image
  
}


trait Parsing[Image] extends Palette[Image] {
  import scala.util.parsing.combinator._
  
  trait ParsingSprite extends BaseSprite {
    var width = 48
    var height = 30
    var xoffset = 0
    var yoffset = 3
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
  
  type Sprite >: Null <: ParsingSprite
  
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


trait Caching {
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


trait DefaultPalette[Img] extends Palette[Img] with Parsing[Img] with Images[Img] with Caching {
  
  def bufferedImage(name: String) = javax.imageio.ImageIO.read(pngStream(name))
  
  def newImageFromPngStream(stream: java.io.InputStream) = toImg(javax.imageio.ImageIO.read(stream))
  
  def newImage(name: String) = toImg(bufferedImage(name))
  
  def newSprite(imgs: Seq[Img]): Sprite
  
  def toImg(img: JBImage): Img
  
  def width(img: Img): Int
  
  def height(img: Img): Int
  
  def NullSprite: Sprite
  
  type Cachee = Sprite
  
  def loadSprite(name: String): Sprite = {
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
    val s = newSprite(images.map(toImg(_)))
    val dschunk = pngimage.getTextChunk("descriptor")
    if (dschunk != null) parseSpriteInfo(s, dschunk.getText)
    
    // set known
    s.width = width(s.img)
    s.height = height(s.img)
    s.frames = images.length
    
    s
  }
  
  def findSprite(name: String): Sprite = {
    val cached = getCache(name)
    if (cached != null) cached else {
      val s = loadSprite(name)
      addCache(name, s)
      s
    }
  }
  
  def sprite(c: Character): Sprite = c match {
    case NoCharacter => NullSprite
    case c => findSprite(c.identifier)
  }
  
  def sprite(e: Effect) = NullSprite
  
  def sprite(t: Slot) = findSprite(t.identifier)
  
  def wall(t: Slot) = findSprite(t.identifier + "-wall")
  
  def maxSpriteHeight = Sprites.maxheight
  
}


class DefaultGLPalette extends DefaultPalette[Null] {
  type Img = Null
  
  class Sprite(val images: Seq[Img]) extends ParsingSprite {
    def image(frame: Int) = images(frame)
  }
  
  def newSprite(imgs: Seq[Img]) = new Sprite(imgs)
  
  def toImg(img: JBImage) = null
  
  def width(img: Img) = 0
  
  def height(img: Img) = 0
  
  object NullSprite extends Sprite(null)
  
}

  
class DefaultSwingPalette extends DefaultPalette[JBImage] {
  
  /* types */
  
  type Img = JBImage
  
  class Sprite(val images: Seq[Img]) extends ParsingSprite {
    def image(frame: Int) = images(frame)
  }

  object NullSprite extends Sprite(null)
  
  def newSprite(imgs: Seq[Img]) = new Sprite(imgs)
  
  def toImg(img: JBImage) = img
  
  def width(img: Img) = img.getWidth(null)
  
  def height(img: Img) = img.getHeight(null)
  
}




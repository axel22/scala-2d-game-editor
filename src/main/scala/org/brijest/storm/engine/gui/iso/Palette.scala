/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import org.brijest.storm.util.CircularQueue
import collection._
import model._



trait Palette {
  
  trait Sprite {
    def width: Int
    def height: Int
    def xoffset: Int
    def yoffset: Int
  }
  
  def sprite(c: Character): Sprite
  
  def sprite(e: Effect): Sprite
  
  def sprite(t: Slot): Sprite
  
  def maxSpriteHeight: Int
  
}


trait Parsing extends Palette {
  import scala.util.parsing.combinator._
  
  trait Sprite extends super.Sprite {
    var width = 0
    var height = 0
    var xoffset = 0
    var yoffset = 0
    
    def set(kv: (String, String)) = kv match {
      case ("width", w) => width = w.toInt
      case ("height", h) => height = h.toInt
      case ("xoffset", xo) => xoffset = xo.toInt
      case ("yoffset", yo) => yoffset = yo.toInt
    }
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
        case props => for (p <- props) s.set _
      }
      def prop: Parser[(String, String)] = ident ~ "=" ~ value ^^ {
        case nm ~ _ ~ v => (nm, v)
      }
      def value: Parser[String] = numericLit | stringLit
    }
    
    new SpriteInfoParser parse()
  }
  
}


trait Images extends Palette {
  type Image
  
  def newImage(name: String): Image
}


trait Caching extends Palette {
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


class DefaultSwingPalette extends Palette with Parsing with Images with Caching {
  
  /* types */
  
  type Image = java.awt.image.BufferedImage
  
  def newImage(name: String) = javax.imageio.ImageIO.read(pngStream(name))
  
  class Sprite extends super[Parsing].Sprite {
    var image: Image = null
  }
  
  object NullSprite extends Sprite
  
  def newSprite = new Sprite
  
  type Cachee = Sprite
  
  /* internal */
  
  def loadSprite(name: String) = {
    val pngimage = new com.sixlegs.png.PngImage()
    val s = newSprite
    val image = pngimage.read(pngStream(name), true)
    val dschunk = pngimage.getTextChunk("descriptor")
    if (dschunk != null) parseSpriteInfo(s, dschunk.getText)
    s.image = image
    // TODO remove this:
    s.height = 50
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
  
  def sprite(t: Slot) = null
  
  def maxSpriteHeight = Sprites.maxheight
  
}




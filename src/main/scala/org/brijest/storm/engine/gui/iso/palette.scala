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
import java.awt.color.ColorSpace
import java.awt.image._
import java.nio.ByteBuffer
import javax.media.opengl._
import org.brijest.storm.util._
import collection._
import model._



trait PaletteCanvas extends Canvas {
  
  trait Palette {
    
    trait BaseSprite {
      def img: Img = image(0)
      def image(frame: Int): Img
      def width: Int
      def height: Int
      def xoffset: Int
      def yoffset: Int
      def frames: Int
      def animated: Boolean
    }
    
    type Sprite >: Null <: BaseSprite
    
    def sprite(c: Character): Sprite
    
    def top(c: Character): Sprite
    
    def sprite(e: Effect): Sprite
    
    def sprite(t: Slot): Sprite
    
    def wall(t: Slot): Sprite
    
    def walltop(t: Slot): Sprite
    
    def edges(t: Slot): Sprite
    
    def maxSpriteHeight: Int
    
    def newImageFromPngStream(stream: java.io.InputStream): Img
    
  }
  
  trait Parsing extends Palette {
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

  trait Images extends Palette {
    def newImage(name: String): Img
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

  trait DefaultPalette extends Palette with Parsing with Images with Caching {
    
    def bufferedImage(name: String) = javax.imageio.ImageIO.read(pngStream(name))
    
    def newImageFromPngStream(stream: java.io.InputStream) = toImg(javax.imageio.ImageIO.read(stream))
    
    def newImage(name: String) = toImg(bufferedImage(name))
    
    def newSprite(imgs: Seq[Img]): Sprite
    
    def toImg(img: BufferedImage): Img
    
    def width(img: Img): Int
    
    def height(img: Img): Int
    
    def NullSprite: Sprite
    
    type Cachee = Sprite
    
    def loadSprite(name: String): Sprite = {
      val pngimage = new com.sixlegs.png.AnimatedPngImage()
      val tmpfile = java.io.File.createTempFile("storm", "tmp")
      tmpfile.deleteOnExit()
      val pngis = pngStream(name)
      assert(pngis != null, "sprite " + name + " does not exist")
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
    
    def top(c: Character): Sprite = c match {
      case NoCharacter => NullSprite
      case c => findSprite(c.topIdentifier)
    }
    
    def sprite(e: Effect) = NullSprite
    
    def sprite(t: Slot) = findSprite(t.identifier)
    
    def wall(t: Slot) = findSprite(t.wallIdentifier)
    
    def walltop(t: Slot) = findSprite(t.topIdentifier)
    
    def edges(t: Slot) = findSprite(t.edgeIdentifier)
    
    def maxSpriteHeight = Sprites.maxheight
    
  }

  val palette: Palette
  
  def imageFromPngStream(stream: java.io.InputStream) = palette.newImageFromPngStream(stream)
  
}


trait GLPaletteCanvas extends PaletteCanvas {
  case class Img()(val width: Int, val height: Int, val texWidth: Int, val texHeight: Int, val data: ByteBuffer) {
    private var stamp = -1L
    var texno = -1
    
    def cache(newstamp: Long, gl: GL2) = if (stamp != newstamp) {
      // TODO free resources
      gl.glGenTextures(1, texptr, 0)
      gl.glBindTexture(GL.GL_TEXTURE_2D, texptr(0))
      gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1)
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP)
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP)
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR)
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR)
      gl.glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE)
      gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, data)
      stamp = newstamp
      texno = texptr(0)
    }
  }
  
  val texptr = new Array[Int](1)
  
  final class DefaultGLPalette extends DefaultPalette {
    
    class Sprite(val images: Seq[Img]) extends ParsingSprite {
      def image(frame: Int) = images(frame)
    }
    
    def newSprite(imgs: Seq[Img]) = new Sprite(imgs)
    
    def toImg(img: BufferedImage) = {
      val iw = img.getWidth(null)
      val ih = img.getHeight(null)
      val w = ceilpow2(iw)
      val h = ceilpow2(ih)
      val raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, iw, ih, 4, null)
      val colorModel = new ComponentColorModel(
        ColorSpace.getInstance(ColorSpace.CS_sRGB),
	Array(8, 8, 8, 8),
	true,
	false,
	java.awt.Transparency.TRANSLUCENT,
	DataBuffer.TYPE_BYTE)
      val nimg = new BufferedImage(colorModel, raster, false, null)
      
      nimg.createGraphics.drawImage(img, null, null)
      
      val data = ByteBuffer.wrap(raster.getDataBuffer.asInstanceOf[DataBufferByte].getData)
      
      data.position(0)
      data.mark()
      
      Img()(iw, ih, w, h, data)
    }
    
    def width(img: Img) = img.width
    
    def height(img: Img) = img.height
    
    object NullSprite extends Sprite(null)
    
  }

}


trait SwingPaletteCanvas extends PaletteCanvas {

  type Img = BufferedImage
  
  class DefaultSwingPalette extends DefaultPalette {
    
    /* types */
    
    class Sprite(val images: Seq[Img]) extends ParsingSprite {
      def image(frame: Int) = images(frame)
    }
    
    object NullSprite extends Sprite(null)
    
    def newSprite(imgs: Seq[Img]) = new Sprite(imgs)
    
    def toImg(img: BufferedImage) = img
    
    def width(img: Img) = img.getWidth(null)
    
    def height(img: Img) = img.getHeight(null)
    
  }
  
}


/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.sprite



import model._
import collection._



@deprecated
trait SpriteUI extends UI {
  var pos = (0, 0)
  var playerId: PlayerId = invalidPlayerId
  var engine: Option[Engine] = None
  
  trait Palette {
    def sprite(e: EntityView): Sprite
    def sprite(t: Slot): Sprite
  }
  
  def palette: Palette
  
  def width: Int
  
  def height: Int
  
  def swdt = 32
  
  def shgt = 32
  
  def update(actions: Seq[Action], area: AreaView, state: Engine.State) = refresh(area, state)
  
  def message(msg: String) {}
  
  def refresh(area: AreaView, state: Engine.State) = {
    val wslots = (width / swdt + 1) min area.terrain.dimensions._1
    val hslots = (height / shgt + 1) min area.terrain.dimensions._2
    val pal = palette
    
    // draw terrain
    val x0 = pos._1
    var x = x0
    val xuntil = x + wslots
    val y0 = pos._2
    var y = y0
    val yuntil = y + hslots
    while (y < yuntil) {
      while (x < xuntil) {
        val t = area.terrain(x, y)
        val s = pal.sprite(t)
        s.draw((x - x0) * swdt, (y - y0) * shgt, 0)
        x += 1
      }
      x = pos._1
      y += 1
    }
    
    // draw items
    x = x0
    y = y0
    while (y < yuntil) {
      while (x < xuntil) {
        val items = area.items.locs(x, y)
        items match {
          case i :: is =>
            val s = pal.sprite(i)
            s.draw((x - x0) * swdt, (y - y0) * shgt, 0)
          case Nil => // skip
        }
        x += 1
      }
      x = pos._1
      y += 1
    }
    
    // draw characters
    x = x0
    y = y0
    while (y < yuntil) {
      while (x < xuntil) {
        area.characters.locs(x, y) match {
          case NoCharacter => // skip
          case c =>
            val s = pal.sprite(c)
            s.draw((x - x0) * swdt, (y - y0) * shgt, 0)
        }
        x += 1
      }
      x = pos._1
      y += 1
    }
  }
  
}


object SpriteUI {
  import scala.util.parsing.combinator._
  
  def pngStream(group: String): java.io.InputStream = {
    getClass.getResourceAsStream("/" + group + ".png")
  }
  
  trait ImageInfo {
    def apply(idx: String, frame: Int): ((Int, Int), (Int, Int), (Int, Int))
    def position(idx: String, frame: Int) = apply(idx, frame)._1
    def size(idx: String, frame: Int) = apply(idx, frame)._2
    def offset(idx: String, frame: Int) = apply(idx, frame)._3
  }
  
  def imageInfo(group: String): ImageInfo = {
    val desc = getClass.getResourceAsStream("/" + group + ".dsc")
    try {
      val table = parseImageInfo(group, org.apache.commons.io.IOUtils.toString(desc, "UTF-8"))
      
      new ImageInfo {
        def apply(idx: String, frame: Int) = {
          val seq = table(idx)(frame).split(',').map(_.toInt)
          ((seq(0), seq(1)), (seq(2), seq(3)), (seq(4), seq(5)))
        }
      }
    } finally {
      desc.close()
    }
  }
  
  def parseImageInfo(group: String, s: String): mutable.Map[String, Seq[String]] = {
    class ImageInfoParser extends syntactical.StandardTokenParsers {
      val info = mutable.Map[String, Seq[String]]()
      
      lexical.delimiters ++= List("{", "}", ",", ":", "=", ";", ".", "-")
      
      entries(new lexical.Scanner(s)) match {
        case Success(obj, _) => obj
        case Failure(msg, _) => 
        case Error(msg, _) => 
      }
      
      def entries: Parser[Any] = rep(entry)
      def entr = ident ^^ { case x => println(x) }
      def entry: Parser[Unit] = ident ~ "=" ~ "{" ~ frames ~ "}" ^^ {
        case nm ~ _ ~ _ ~ frames ~ _ => info.put(group + "." + nm, frames)
      }
      def frames: Parser[Seq[String]] = rep(frame) ^^ {
        case sq => sq.toBuffer
      }
      def frame: Parser[String] = number ~ ":" ~ repsep(number, ",") ~ ";" ^^ {
        case _ ~ _ ~ numbers ~ _ => numbers.mkString(",")
      }
      def number = posnumber | negnumber
      def posnumber = super.numericLit ^^ { _.toInt }
      def negnumber = "-" ~ posnumber ^^ {
        case _ ~ n => -n.toInt
      }
    }
    
    (new ImageInfoParser).info
  }
  
}


trait Sprite {
  def draw(x: Int, y: Int, frame: Int)
}


object NullSprite extends Sprite {
  def draw(x: Int, y: Int, frame: Int) {}
}

/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine



import model._
import collection._



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
    
    // draw background
    // TODO
    
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
    /*
    x = x0
    y = y0
    while (y < yuntil) {
      while (x < xuntil) {
        val items = area.items.locs(x, y)
        items match {
          case i :: is =>
            val s = pal.sprite(i)
            s.draw((x - x0) * swdt, (y - y0) * shgt, 0)
            x += 1
          case Nil => // skip
        }
      }
      x = pos._1
      y += 1
    }
    
    // draw characters
    x = x0
    y = y0
    while (y < yuntil) {
      while (x < xuntil) {
        val c = area.characters.locs(x, y)
        val s = pal.sprite(c)
        s.draw((x - x0) * swdt, (y - y0) * shgt, 0)
        x += 1
      }
      x = pos._1
      y += 1
    }
    */
  }
  
}


object SpriteUI {
  def pngStream(group: String): java.io.InputStream = {
    getClass.getResourceAsStream("/" + group + ".png")
  }
  def imageInfo(group: String): ImageInfo = {
    val s = getClass.getResourceAsStream("/" + group + ".dsc")
    try {
      val props = new java.util.Properties
      props.load(s)
      val propmap = mutable.HashMap[String, String]()
      val sz = props.stringPropertyNames.size
      val table = new Array[String](sz)
      for (nm <- JavaConversions.asScalaSet(props.stringPropertyNames)) {
        val v = props.getProperty(nm)
        val idx = nm.toInt
        table(idx) = v
      }
      
      new ImageInfo {
        def apply(idx: Int) = {
          val seq = table(idx).split(',').map(_.toInt)
          ((seq(0), seq(1)), (seq(2), seq(3)), (seq(4), seq(5)))
        }
      }
    } finally {
      s.close()
    }
  }
}


trait ImageInfo {
  def apply(idx: Int): ((Int, Int), (Int, Int), (Int, Int))
  def position(idx: Int) = apply(idx)._1
  def size(idx: Int) = apply(idx)._2
  def offset(idx: Int) = apply(idx)._3
}


trait Sprite {
  def draw(x: Int, y: Int, frame: Int)
}


object NullSprite extends Sprite {
  def draw(x: Int, y: Int, frame: Int) {}
}

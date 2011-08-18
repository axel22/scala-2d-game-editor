/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import model._
import collection._



trait Canvas {
  type Img
  
  def imageFromPngStream(stream: java.io.InputStream): Img
  
  trait DrawAdapter {
    def setColor(r: Int, g: Int, b: Int)
    def setFontSize(sz: Float)
    def drawLine(x1: Int, y1: Int, x2: Int, y2: Int)
    def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
    def drawPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int)
    def drawString(s: String, x: Int, y: Int)
    def drawImage(image: Img, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int)
    def fillPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int)
  }
}


trait IsoCanvas extends Canvas {
  lazy val stars = imageFromPngStream(pngStream("stars"))
  val deppool = new MemoryPool(new DepNode)
  val infopool = new MemoryPool(new Info)
  var infos: Array[Info] = null
  val xrect = new Array[Int](5)
  val yrect = new Array[Int](5)
  
  val oneone = (1, 1);
  
  final class Info extends Linked[Info] {
    var top: (Int, Int) = null
    var deps: DepNode = null
    var dims: (Int, Int) = oneone
    var drawn = false
    def isTop = deps ne null
    def reset() {
      top = null
      deps = null
      dims = oneone
      drawn = false
    }
    def contains(x0: Int, y0: Int, x: Int, y: Int) = x >= x0 && y >= y0 && x < (x0 + dims._1) && y < (y0 + dims._2)
    def leftXY(x0: Int, y0: Int) = (x0, y0 + dims._2 - 1)
    def rightXY(x0: Int, y0: Int) = (x0 + dims._1 - 1, y0)
    def lowestV(x0: Int, y0: Int, area: AreaView) = {
      var minv = Int.MaxValue
      var mins = area.terrain(x0, y0)
      val xu = x0 + dims._1
      val yu = y0 + dims._2
      var x = x0
      var y = y0
      while (x < xu) {
        while (y < yu) {
          val s = area.terrain(x, y)
          val v = iso2planar_v(x, y, s.height, area.sidelength).toInt
          if (v < minv) {
            minv = v
            mins = s
          }
          y += 1
        }
        y = 0
        x += 1
      }
      minv
    }
  }
  
  final class DepNode extends Linked[DepNode] {
    private val array = new Array[Int](32)
    private var sz = 0
    def reset() = sz = 0
    def add(x: Int, y: Int): DepNode = if (sz < 32) {
      array(sz) = x
      array(sz + 1) = y
      sz += 2
      this
    } else {
      val dp = deppool.allocate
      dp.next = this
      dp.add(x, y)
      dp
    }
    @annotation.tailrec def foreach[U](f: (Int, Int) => U): Unit = {
      var i = 0
      while (i < sz) {
        f(array(i), array(i + 1))
        i += 2
      }
      if (next ne null) next.foreach(f)
    }
    override def toString: String = array.mkString("[", ",", "]") + " --> " + (if (next ne null) next.toString else "")
  }
  
  def width: Int
  
  def height: Int
  
  def slotheight: Int
  
  def slotwidth = slotheight * ratio
  
  def ratio = 2
  
  def levelheight = 16
  
  object drawing {
    def outline = true
    def indices = true
    def background = true
  }
  
  def pos: (Int, Int)
  
  def framespersec = 35
  
  def framelength = (1000.0 / framespersec).toInt
  
  @inline final def planar2iso(u: Double, v: Double, mapsz: Int): (Double, Double) =
    (v / slotheight + u / slotwidth - mapsz / 2, v / slotheight - u / slotwidth + mapsz / 2);
  
  @inline final def planar2iso(u: Int, v: Int, mapsz: Int): (Double, Double) =
    planar2iso(u.toDouble, v.toDouble, mapsz)
  
  @inline final def iso2planar_u(x: Double, y: Double, z: Double, mapsz: Int): Double = (mapsz - y + x) * slotwidth / 2
  
  @inline final def iso2planar_v(x: Double, y: Double, z: Double, mapsz: Int): Double = (x + y) * slotheight / 2 - z * levelheight
  
  @inline final def iso2planar(x: Double, y: Double, z: Double, mapsz: Int): (Double, Double) =
    (iso2planar_u(x, y, z, mapsz), iso2planar_v(x, y, z, mapsz));
  
  @inline final def iso2planar(x: Int, y: Int, z: Int, mapsz: Int): (Double, Double) =
    iso2planar(x.toDouble, y.toDouble, z.toDouble, mapsz)
  
  def maxPlanarWidth(mapsz: Int) = iso2planar(mapsz, 0, 0, mapsz)._1 + slotwidth
  
  def maxPlanarHeight(mapsz: Int) = iso2planar(mapsz, mapsz, 0, mapsz)._2 + slotheight
  
  def characterSprite(c: CharacterView): Sprite
  
  def maxSpriteHeight: Int
  
  def redrawBackground(area: AreaView, a: DrawAdapter) = if (drawing.background) {
    val w = 800
    val h = 600
    val x0 = pos._1 / 4 % w
    val y0 = pos._2 / 4 % h
    for (x <- -1 to (width / w + 1); y <- -1 to (height / h + 1))
      a.drawImage(stars, x * w - x0, y * h - y0, x * w + w - x0, y * h + h - y0, 0, 0, w, h)
  }
  
  def redraw(area: AreaView, engine: Engine.State, a: DrawAdapter) {
    redrawBackground(area, a)
    
    // determine region
    val (u0, v0) = pos
    val pw = width
    val ph = height + area.maxheight() * levelheight + maxSpriteHeight
    val (xtl, ytl) = planar2iso(u0, v0, area.sidelength)
    val (xtr, ytr) = planar2iso(u0 + pw, v0, area.sidelength)
    val (xbr, ybr) = planar2iso(u0 + pw, v0 + ph, area.sidelength)
    val (xbl, ybl) = planar2iso(u0, v0 + ph, area.sidelength)
    val x0 = xtl.toInt
    val y0 = ytr.toInt
    val w = (xbr - x0).toInt
    val h = (ybl - y0).toInt
    
    @inline def onscreen(x: Int, y: Int) = x >= x0 && y >= y0 && x < (x0 + w) && y < (y0 + h)
    object slotinfo {
      @inline def apply(x: Int, y: Int) = if (onscreen(x, y)) infos((y - y0) * w + (x - x0)) else null
      @inline def update(x: Int, y: Int, i: Info) = infos((y - y0) * w + (x - x0)) = i
    }
    
    // initialize and group infos
    if ((infos eq null) || infos.length != w * h) infos = new Array[Info](w * h)
    for (x <- x0 until (x0 + w); y <- y0 until (y0 + h)) if (area.contains(x, y)) area.characters(x, y) match {
      case NoCharacter =>
        val info = infopool.allocate
        info.deps = deppool.allocate
        slotinfo(x, y) = info
      case c =>
        val info = infopool.allocate
        if (c.pos.equals(x, y)) {
          info.deps = deppool.allocate
          info.dims = c.dimensions()
        } else info.top = c.pos().toPair
        slotinfo(x, y) = info
    }
    
    // compute dependencies - iterate over all the infos diagonal-wise
    def dependencies(x: Int, y: Int) {
      val info = slotinfo(x, y)
      if ((info ne null) && info.isTop) {
        val cspr = characterSprite(area.characters(x, y))
        val vhi = info.lowestV(x, y, area) - cspr.height
        val (u, v) = iso2planar(x, y, area.terrain(x, y).height, area.sidelength)
        val (xtop, ytop) = planar2iso(u, vhi, area.sidelength)
        val (xl, yl) = info.leftXY(x, y)
        val (xr, yr) = info.rightXY(x, y)
        val uleft = iso2planar_u(xl, yl, 0, area.sidelength) - slotwidth * 2
        val uright = iso2planar_u(xr, yr, 0, area.sidelength) + slotwidth * 2
        
        // add everything in the rectangle to the dependency list
        for (xp <- xtop.toInt to xr; yp <- ytop.toInt to yl) {
          val up = iso2planar_u(xp, yp, 0, area.sidelength)
          if (up > uleft && up < uright && !info.contains(x, y, xp, yp)) {
            val depinfo = slotinfo(xp, yp)
            if (depinfo ne null) {
              if (depinfo.isTop) info.deps = info.deps.add(xp, yp)
              else info.deps = info.deps.add(depinfo.top._1, depinfo.top._2)
            }
          }
        }
      }
    }
    for (i <- 0 until h; x <- 0 to i; y = i - x) dependencies(x0 + x, y0 + y)
    for (i <- 0 until h; x <- i until h; y = h + i - x) dependencies(x0 + x, y0 + y)
    
    // reverse drawing
    def drawTop(x: Int, y: Int, info: Info) {
      import a._
      if (drawing.outline) {
        // draw terrain and sides
        @inline implicit def double2int(d: Double) = d.toInt
        @inline def drawTerrain(up: Int, vp: Int) {
          xrect(0) = up - slotwidth / 2
          xrect(1) = up
          xrect(2) = up + slotwidth / 2
          xrect(3) = up
          xrect(4) = up - slotwidth / 2
          yrect(0) = vp
          yrect(1) = vp - slotheight / 2
          yrect(2) = vp
          yrect(3) = vp + slotheight / 2
          yrect(4) = vp
          setColor(0, 0, 0)
          fillPoly(xrect, yrect, 5)
          setColor(0, 100, 200)
          drawPoly(xrect, yrect, 5)
          setFontSize(8)
          if (drawing.indices) drawString("%s, %s".format(x, y), up - slotwidth / 4, vp)
        }
        @inline def drawTerrainSides(slot: Slot, xp: Int, yp: Int, up: Int, vp: Int) {
          val lslothgt = if (area.contains(xp, yp + 1)) area.terrain(xp, yp + 1).height else 0
          if (slot.height > lslothgt) {
            val lu = iso2planar_u(xp, yp + 1, lslothgt, area.sidelength) - u0
            val lv = iso2planar_v(xp, yp + 1, lslothgt, area.sidelength) - v0
            xrect(0) = up - slotwidth / 2
            xrect(1) = up
            xrect(2) = lu + slotwidth / 2
            xrect(3) = lu
            xrect(4) = up - slotwidth / 2
            yrect(0) = vp
            yrect(1) = vp + slotheight / 2
            yrect(2) = lv
            yrect(3) = lv - slotheight / 2
            yrect(4) = vp
            setColor(0, 0, 0)
            fillPoly(xrect, yrect, 5)
            setColor(0, 100, 200)
            drawPoly(xrect, yrect, 5)
          }
          val rslothgt = if (area.contains(xp + 1, yp)) area.terrain(xp + 1, yp).height else 0
          if (slot.height > rslothgt) {
            val lu = iso2planar_u(xp + 1, yp, rslothgt, area.sidelength) - u0
            val lv = iso2planar_v(xp + 1, yp, rslothgt, area.sidelength) - v0
            xrect(0) = up
            xrect(1) = up + slotwidth / 2
            xrect(2) = lu
            xrect(3) = lu - slotwidth / 2
            xrect(4) = up
            yrect(0) = vp + slotheight / 2
            yrect(1) = vp
            yrect(2) = lv - slotheight / 2
            yrect(3) = lv
            yrect(4) = vp + slotheight / 2
            setColor(0, 0, 0)
            fillPoly(xrect, yrect, 5)
            setColor(0, 100, 200)
            drawPoly(xrect, yrect, 5)
          }
        }
        val slot = area.terrain(x, y)
        val u = iso2planar_u(x, y, slot.height, area.sidelength) - u0
        val v = iso2planar_v(x, y, slot.height, area.sidelength) - v0
        drawTerrain(u, v)
        drawTerrainSides(slot, x, y, u, v)
        
        // draw character
        // TODO
      } else {
        // TODO
        unsupported
      }
    }
    def reverseDraw(x: Int, y: Int) {
      val info = slotinfo(x, y)
      if (info != null && !info.drawn) {
        if (info.isTop) {
          info.deps.foreach((xp, yp) => reverseDraw(xp, yp))
          drawTop(x, y, info)
          info.drawn = true
        } else {
          reverseDraw(info.top._1, info.top._2)
          info.drawn = true
        }
      }
    }
    for (i <- 0 until h; x <- 0 to i; y = i - x) reverseDraw(x0 + x, y0 + y)
    for (i <- 0 until h; x <- i until h; y = h + i - x) reverseDraw(x0 + x, y0 + y)
   
    // dispose dependencies
    for (i <- 0 until infos.length) {
      val info = infos(i)
      if (info != null) {
        infos(i) = null
        if (info.deps ne null) deppool.dispose(info.deps)
        infopool.dispose(info)
      }
    }
  }
  
}

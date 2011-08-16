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



trait IsoCanvas {
  val deppool = new MemoryPool(new DepNode)
  val infopool = new MemoryPool(new Info)
  var infos: Array[Info] = null
  
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
    def leftXY(x0: Int, y0: Int) = (x0, y0 + dims._2)
    def rightXY(x0: Int, y0: Int) = (x0 + dims._1, y0)
    def lowestV(x0: Int, y0: Int, area: AreaView) = {
      var minv = Int.MaxValue
      var mins = area.terrain(x0, y0)
      val xu = x0 + dims._1
      val yu = y0 + dims._2
      var x, y = 0
      while (x < xu) {
        while (y < yu) {
          val s = area.terrain(x, y)
          val v = iso2planar(x, y, s.height, area.sidelength)._2.toInt
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
      val dp = deppool.create
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
  }
  
  def width: Int
  
  def height: Int
  
  def slotheight: Int
  
  def slotwidth = slotheight * ratio
  
  def ratio = 2
  
  def levelheight = 16
  
  def outline = true
  
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
  
  def draw(area: AreaView, a: DrawAdapter) {
    // determine region
    val (u0, v0) = pos
    val pw = width
    val ph = height
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
        val info = infopool.create
        info.deps = deppool.create
        slotinfo(x, y) = info
      case c =>
        val info = infopool.create
        if (c.pos.equals(x, y)) {
          info.deps = deppool.create
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
    for (i <- 0 until h; x <- 0 until i; y = i - x) dependencies(x, y)
    for (i <- 1 until h; x <- i until h; y = h - x) dependencies(x, y)
    
    // reverse drawing
    def draw(x: Int, y: Int, info: Info) {
      import a._
      if (outline) {
        val (u, v) = iso2planar(x, y, area.terrain(x, y).height, area.sidelength)
        
        // draw terrain and sides
        setColor(0, 100, 200)
        drawLine(u - slotwidth / 2, v, u, v - slotheight / 2)
        drawLine(u, v - slotheight / 2, u + slotwidth / 2, v)
        drawLine(u + slotwidth / 2, v, u, v + slotheight / 2)
        drawLine(u, v + slotheight / 2, u - slotwidth / 2, v)
        // TODO sides
        
        // draw character
        // TODO
      } else {
        // TODO
        unsupported
      }
    }
    def reverseDraw(x: Int, y: Int) {
      println(x, y)
      val info = slotinfo(x, y)
      if (info.isTop && !info.drawn) {
        info.deps.foreach((xp, yp) => reverseDraw(xp, yp))
        draw(x, y, info)
        info.drawn = true
      }
    }
    for (i <- 0 until h; x <- 0 until i; y = i - x) reverseDraw(x, y)
    for (i <- 1 until h; x <- i until h; y = h - x) reverseDraw(x, y)
   
    // dispose dependencies
    for (i <- 0 until infos.length) {
      val info = infos(i)
      infos(i) = null
      if (info.deps ne null) deppool.dispose(info.deps)
      infopool.dispose(info)
    }
  }
  
}
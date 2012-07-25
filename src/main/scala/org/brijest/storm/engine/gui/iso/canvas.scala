/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package gui.iso



import model._
import collection._
import org.scalapool._



trait Canvas {
  type Img
  
  trait DrawAdapter {
    def setColor(r: Int, g: Int, b: Int, alpha: Int)
    def setLineWidth(w: Float)
    def setFontSize(sz: Float)
    def drawLine(x1: Int, y1: Int, x2: Int, y2: Int)
    def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
    def drawPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int)
    def drawString(s: String, x: Int, y: Int)
    def drawImage(image: Img, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int)
    def fillPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int)
    def fillRect(x1: Int, y1: Int, w: Int, h: Int)
  }
  
  def tileCoord(area: AreaView, u: Int, v: Int): (Int, Int)
  
  /* constants */
  
  final def tileWidth = 48
  final def tileHeight = 24
  final def wallHeight = 64
  final def wallWidth = tileWidth
  final def edgesheetWidth = 192
  final def edgesheetHeight = 60
  final def topHeight = 32
  final def topWidth = tileWidth
  
  /* see docs/edges.jpg for explanation */
  val edgelut = new Array[Int](32)
  
  {
    object edge {
      def update(binidx: String, v: Int) {
        val idx = Integer.parseInt(binidx, 2)
        edgelut(idx) = v
      }
    }
    
    edge("00010") = 3
    edge("00100") = 9
    edge("00110") = 9
    edge("00001") = 13
    edge("00011") = 13
    edge("00101") = 7
    edge("00111") = 7
    
    edge("01010") = 1
    edge("01100") = 11
    edge("01110") = 11
    edge("01001") = 15
    edge("01011") = 15
    edge("01101") = 5
    edge("01111") = 5
    
    edge("10010") = 2
    edge("10100") = 8
    edge("10110") = 8
    edge("10001") = 12
    edge("10011") = 12
    edge("10101") = 6
    edge("10111") = 6
    
    edge("11010") = 0
    edge("11100") = 10
    edge("11110") = 10
    edge("11001") = 14
    edge("11011") = 14
    edge("11101") = 4
    edge("11111") = 4
  }
  
}


trait IsoCanvas extends Canvas with PaletteCanvas {
  lazy val stars = imageFromPngStream(pngStream("stars"))
  val deppool: singlethread.FreeList[DepNode] = new singlethread.FreeList(new DepNode)({ _.reset() }) {
    override def allocate() = {
      val dp = super.allocate()
      if (dp.next != null) {
        deppool.dispose(dp.next)
        dp.next = null
      }
      dp
    }
  }
  val infopool = Allocator.singleThread.freeList(new Info) { _.reset() }
  var infos: Array[Info] = null
  var effectbitmap: Array[Boolean] = null
  var effectmap = mutable.Map[(Int, Int), mutable.Set[Info]]()
  val hiddenCharacters = mutable.Set[Character]()
  val oneone = (1, 1);
  
  final class Info extends singlethread.Linkable[Info] {
    var top: Pos = null
    var deps: DepNode = null
    var effectdeps: DepNode = null
    var dims: (Int, Int) = oneone
    var drawn = false
    var id = 0L
    var effect: Effect = null
    def isTop = deps ne null
    def isEffect = effect ne null
    def reset() {
      top = null
      deps = null
      dims = oneone
      drawn = false
      id = 0L
      effect = null
    }
    def addDep(x: Int, y: Int, other: Info) {
      if (other.isTop) deps = deps.add(x, y)
      else deps = deps.add(other.top.x, other.top.y)
    }
    def addEffectDep(x: Int, y: Int, other: Info) {
      if (effectdeps eq null) effectdeps = deppool.allocate()
      if (other.isTop) effectdeps = effectdeps.add(x, y)
      else effectdeps = effectdeps.add(other.top.x, other.top.y)
    }
    def sameTop(thisxp: Int, thisyp: Int, other: Info, xp: Int, yp: Int) = if (isTop) {
      if (other.isTop) thisxp == xp && thisyp == yp
      else thisxp == other.top.x && thisyp == other.top.y
    } else {
      if (other.isTop) this.top.x == xp && this.top.y == yp
      else this.top.x == other.top.x && this.top.y == other.top.y
    }
    @inline def foreach[U](x0: Int, y0: Int)(f: (Int, Int) => U) = foreachNW2SE(x0, y0, dims._1, dims._2)(f)
    def contains(x0: Int, y0: Int, x: Int, y: Int) = x >= x0 && y >= y0 && x < (x0 + dims._1) && y < (y0 + dims._2)
    def leftXY(x0: Int, y0: Int) = (x0, y0 + dims._2 - 1)
    def rightXY(x0: Int, y0: Int) = (x0 + dims._1 - 1, y0)
    def bottomXY(x0: Int, y0: Int) = (x0 + dims._1 - 1, y0 + dims._2 - 1)
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
  
  final class DepNode extends singlethread.Linkable[DepNode] {
    val array = new Array[Int](32)
    var sz = 0
    var next: DepNode = null
    def reset() = sz = 0
    def add(x: Int, y: Int): DepNode = if (sz < 32) {
      array(sz) = x
      array(sz + 1) = y
      sz += 2
      this
    } else {
      val dp = deppool.allocate()
      dp.next = this
      dp.add(x, y)
      dp
    }
    @inline def foreach[U](f: (Int, Int) => U): Unit = {
      var curr = this
      while (curr != null) {
        /* traverse contents of this depnode */
        var i = 0
        while (i < curr.sz) {
          f(curr.array(i), curr.array(i + 1))
          i += 2
        }
        /* move to next depnode */
        curr = curr.next
      }
    }
    override def toString: String = array.take(sz).mkString("[", ",", "]") + " --> " + (if (next ne null) next.toString else "")
  }
  
  abstract class Drawer(a: DrawAdapter) {
    val xrect = new Array[Int](5)
    val yrect = new Array[Int](5)
    var transparent = drawing.seethrough
    
    @inline implicit final def double2int(d: Double) = d.toInt
    
    @inline final def rect(x0: Int, x1: Int, x2: Int, x3: Int, y0: Int, y1: Int, y2: Int, y3: Int) {
      xrect(0) = x0
      xrect(1) = x1
      xrect(2) = x2
      xrect(3) = x3
      xrect(4) = x0
      yrect(0) = y0
      yrect(1) = y1
      yrect(2) = y2
      yrect(3) = y3
      yrect(4) = y0
    }
    
    @inline final def drawRect(r: Int, g: Int, b: Int, alpha: Int) {
      a.setColor(0, 0, 0, 255)
      if (!transparent) a.fillPoly(xrect, yrect, 5)
      a.setColor(r, g, b, alpha)
      a.drawPoly(xrect, yrect, 5)
    } 
    
    def random(x: Int, y: Int) = math.abs(Integer.reverseBytes(((x << 8) + y + x + (y << 4)) * 0x9e3775cd) * 0x9e3775cd)
 }
  
  trait TerrainDrawer {
    def drawTerrain(slot: Slot, xp: Int, yp: Int, up: Int, vp: Int, vpuoffs: Int, vpvoffs: Int)
  }
  
  class TerrainSpriteDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends TerrainOutlineDrawer(a, area, u0, v0) {
    import a._
    
    val neighbours = new Array[Slot](8)
    val nbsorted = new Array[Int](3)
    val nbsprites = new Array[AnyRef](8)
    
    def loadNeighboursAndCheckEdge(x: Int, y: Int, layer: Int) = {
      var samelayer = true
      neighbours(0) = area.safeTerrain(x + 0, y + 1)
      if (neighbours(0).layer != layer) samelayer = false
      neighbours(1) = area.safeTerrain(x + 1, y + 1)
      if (neighbours(1).layer != layer) samelayer = false
      neighbours(2) = area.safeTerrain(x + 1, y + 0)
      if (neighbours(2).layer != layer) samelayer = false
      neighbours(3) = area.safeTerrain(x + 1, y - 1)
      if (neighbours(3).layer != layer) samelayer = false
      neighbours(4) = area.safeTerrain(x + 0, y - 1)
      if (neighbours(4).layer != layer) samelayer = false
      neighbours(5) = area.safeTerrain(x - 1, y - 1)
      if (neighbours(5).layer != layer) samelayer = false
      neighbours(6) = area.safeTerrain(x - 1, y + 0)
      if (neighbours(6).layer != layer) samelayer = false
      neighbours(7) = area.safeTerrain(x - 1, y + 1)
      if (neighbours(7).layer != layer) samelayer = false
      var i = 0
      while (i < 8) {
        nbsprites(i) = null
        i += 1
      }
      samelayer
    }
    
    override def drawTerrain(curr: Slot, xp: Int, yp: Int, up: Int, vp: Int, vpuoffs: Int, vpvoffs: Int) {
      // obtain sprite for slot
      val tile = palette.sprite(curr)
      val wall = palette.wall(curr)
      val wtop = palette.walltop(curr)
      val edgeNeeded = !loadNeighboursAndCheckEdge(xp, yp, curr.layer)
      
      // draw terrain walls
      def drawWall(nbheight: Int, nx: Int, ny: Int, walloffset: Int) {
        if (nbheight < curr.height) {
          val lu = iso2planar_u(nx, ny, nbheight, area.sidelength) - u0 + tileWidth / 2
          var lv = iso2planar_v(nx, ny, nbheight, area.sidelength) - v0 + tileHeight / 2 + 3
          val limit = lv - levelheight * (curr.height - nbheight)
          var i = 0
          while (lv > limit) {
            val neededhgt = math.min(wallHeight, lv - limit + tileHeight / 2)
            val frm = random(nx + i, ny + i) % wall.frames
            drawImage(
              wall.image(frm),
              lu - wallWidth / 2 + walloffset, lv - neededhgt, lu + walloffset, lv,
              walloffset, wallHeight - neededhgt, wallWidth / 2 + walloffset, wallHeight
            )
            lv -= wallHeight - tileHeight / 2
            i += 1
          }
        }
      }
      
      def drawTop(nbheight: Int, nx: Int, ny: Int, topoffset: Int) {
        if (nbheight < curr.height) {
          val lu = iso2planar_u(nx, ny, nbheight, area.sidelength) - u0 + tileWidth / 2
          var lv = iso2planar_v(nx, ny, nbheight, area.sidelength) - v0 + tileHeight / 2 + 3
          val limit = lv - levelheight * (curr.height - nbheight)
          drawImage(
            wtop.image(random(nx, ny) % wtop.frames),
            lu - topWidth / 2 + topoffset, limit - topHeight / 2 - 1, lu + topoffset, limit + topHeight / 2 - 1,
            topoffset, 0, topWidth / 2 + topoffset, topHeight
          )
        }
      }
      
      val leftnbheight = if ((yp + 1) < area.terrain.dimensions._2) area.terrain(xp, yp + 1).height else 0
      val rightnbheight = if ((xp + 1) < area.terrain.dimensions._1) area.terrain(xp + 1, yp).height else 0
      
      drawWall(
        leftnbheight,
        xp, yp + 1, wallWidth / 2
      )
      drawWall(
        rightnbheight,
        xp + 1, yp, 0
      )
      
      // draw weak northern outline
      if (neighbours(6).height < curr.height) {
        a.setColor(0, 0, 0, 100)
        a.drawLine(up, vp + 2 + tileHeight / 2, up + tileWidth / 2, vp + 2)
      }
      if (neighbours(4).height < curr.height) {
        a.setColor(0, 0, 0, 100)
        a.drawLine(up + tileWidth / 2, vp + 2, up + tileWidth, vp + 2 + tileHeight / 2)
      }
      
      // draw terrain tile
      def frame = if (!tile.animated) random(xp, yp) % tile.frames else 0
      drawImage(tile.image(frame), up, vp, up + tile.width, vp + tile.height, tile.xoffset, tile.yoffset, tile.xoffset + tile.width, tile.yoffset + tile.height)
      
      // draw wall tops
      drawTop(
        leftnbheight,
        xp, yp + 1, wallWidth / 2
      )
      drawTop(
        rightnbheight,
        xp + 1, yp, 0
      )
      
      // draw edges based on neighbours
      // we detect all the different terrain layers next to curr
      // and draw an edge for each layer
      def drawEdge(sideno: Int) {
        val firstnb = (sideno * 2) % 8
        val currlayer = curr.layer
        val nb = neighbours
        
        val currheight = curr.height
        val sorted = nbsorted
        val nb0 = nb(firstnb + 0)
        val nb1 = nb(firstnb + 1)
        val nb2 = nb((firstnb + 2) % 8)
        val nb0layer = nb0.layer
        val nb1layer = nb1.layer
        val nb2layer = nb2.layer
        val nb0hgt = nb0.height
        val nb1hgt = nb1.height
        val nb2hgt = nb2.height
        val nb0idx = firstnb + 0
        val nb1idx = firstnb + 1
        val nb2idx = (firstnb + 2) % 8
        
        /* inline sort 3 values */ {
          if (nb0layer < nb1layer) {
            if (nb0layer < nb2layer) {
              sorted(0) = nb0idx
              if (nb1layer < nb2layer) {
                sorted(1) = nb1idx
                sorted(2) = nb2idx
              } else {
                sorted(1) = nb2idx
                sorted(2) = nb1idx
              }
            } else {
              sorted(0) = nb2idx
              sorted(1) = nb0idx
              sorted(2) = nb1idx
            }
          } else {
            if (nb1layer < nb2layer) {
              sorted(0) = nb1idx
              if (nb0layer < nb2layer) {
                sorted(1) = nb0idx
                sorted(2) = nb2idx
              } else {
                sorted(1) = nb2idx
                sorted(2) = nb0idx
              }
            } else {
              sorted(0) = nb2idx
              sorted(1) = nb1idx
              sorted(2) = nb0idx
            }
          }
        }
        
        def getNbSprite(idx: Int) = {
          val cached = nbsprites(idx)
          if (cached != null) cached
          else {
            nbsprites(idx) = palette.edges(nb(idx))
            nbsprites(idx)
          }
        }
        
        def drawEdge(slotidx: Int, slot: Slot) {
          val layer = slot.layer
          val sidebits = sideno << 3
          val nb0bits = if (nb0layer == layer && nb0hgt >= currheight) 1 << 2 else 0
          val nb1bits = if (nb1layer == layer && nb1hgt >= currheight) 1 << 1 else 0
          val nb2bits = if (nb2layer == layer && nb2hgt >= currheight) 1 << 0 else 0
          val bits = sidebits | nb0bits | nb1bits | nb2bits
          
          /* locate edge on the spritesheet */
          val number = edgelut(bits)
          val sectionwdt = edgesheetWidth / 4
          val sectionhgt = edgesheetHeight / 2
          val halftilewdt = tileWidth / 2
          val halftilehgt = tileHeight / 2
          val block = number / 2
          val blockx = block % 4
          val blocky = block / 4
          val intilexoff = (1 - (block + blocky) % 2) * (number % 2) * halftilewdt
          val intileyoff = (0 + (block + blocky) % 2) * (number % 2) * halftilehgt + (sectionhgt - tileHeight)
          val xoff = blockx * sectionwdt + intilexoff
          val yoff = blocky * sectionhgt + intileyoff
          val xlen = halftilewdt * (1 + (0 + (block + blocky) % 2))
          val ylen = halftilehgt * (1 + (1 - (block + blocky) % 2))
          val s = getNbSprite(slotidx).asInstanceOf[palette.Sprite]
          val frame = random(xp, yp) % s.frames
          val eu = up + intilexoff
          val ev = vp + intileyoff - (sectionhgt - tileHeight)
          drawImage(s.image(frame), eu - 1, ev + 2, eu + xlen + 1, ev + ylen + 4, xoff - 1, yoff - 1, xoff + xlen + 1, yoff + ylen + 1)
        }
        
        var i = 0
        var aboveLayer = currlayer
        while (i < 3) {
          val slotidx = sorted(i)
          val slot = nb(slotidx)
          val slotlayer = slot.layer
          if (slot.height >= currheight && slotlayer > aboveLayer) {
            aboveLayer = slotlayer
            drawEdge(slotidx, slot)
          }
          i += 1
        }
      }
      
      def drawEdges() {
        drawEdge(0)
        drawEdge(1)
        drawEdge(2)
        drawEdge(3)
      }
      
      if (edgeNeeded) {
        if (!curr.isEmpty) drawEdges()
      }
      
      // draw highlight
      if ((highlight ne null) && highlight._1 == xp && highlight._2 == yp) {
        transparent = true
        super.drawTerrain(curr, xp, yp, up, vp, vpuoffs, vpvoffs)
      }
    }
    
    override def uOffset = tileWidth / 2
    override def vOffset = tileHeight / 2 + 1
    override def outlineR = 0
    override def outlineG = 180
    override def outlineB = 255
    override def outlineAlpha = 120
    override def thickness = 2
  }
  
  class TerrainOutlineDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends Drawer(a) with TerrainDrawer {
    import a._
    
    def uOffset = 0
    def vOffset = 0
    def outlineAlpha = 255
    def outlineR = 0
    def outlineG = 100
    def outlineB = 120
    def thickness = 1
    
    def drawTerrain(slot: Slot, xp: Int, yp: Int, up: Int, vp: Int, vpuoffs: Int, vpvoffs: Int) {
      setLineWidth(thickness)
      rect(
        up + uOffset - tileWidth / 2, up + uOffset, up + uOffset + tileWidth / 2, up + uOffset,
        vp + vOffset, vp - tileHeight / 2 + vOffset, vp + vOffset, vp + tileHeight / 2 + vOffset
      )
      drawRect(outlineR, outlineG, outlineB, outlineAlpha)
      setFontSize(8)
      if (drawing.indices) drawString("%s, %s".format(xp, yp), up - tileWidth / 4, vp)
      
      drawTerrainSides(slot.height, xp, yp, up, vp)
    }
    
    private def drawTerrainSides(currheight: Int, xp: Int, yp: Int, up: Int, vp: Int) {
      val lslothgt = if (area.contains(xp, yp + 1)) area.terrain(xp, yp + 1).height else 0
      if (currheight > lslothgt) {
        val lu = iso2planar_u(xp, yp + 1, lslothgt, area.sidelength) - u0
        val lv = iso2planar_v(xp, yp + 1, lslothgt, area.sidelength) - v0
        rect(
          up + uOffset - tileWidth / 2, up + uOffset, lu + uOffset + tileWidth / 2, lu + uOffset,
          vp + vOffset, vp + vOffset + tileHeight / 2, lv + vOffset, lv + vOffset - tileHeight / 2
        )
        drawRect(outlineR, outlineG, outlineB, outlineAlpha)
      }
      val rslothgt = if (area.contains(xp + 1, yp)) area.terrain(xp + 1, yp).height else 0
      if (currheight > rslothgt) {
        val lu = iso2planar_u(xp + 1, yp, rslothgt, area.sidelength) - u0
        val lv = iso2planar_v(xp + 1, yp, rslothgt, area.sidelength) - v0
        rect(
          up + uOffset, up + uOffset + tileWidth / 2, lu + uOffset, lu + uOffset - tileWidth / 2,
          vp + vOffset + tileHeight / 2, vp + vOffset, lv + vOffset - tileHeight / 2, lv + vOffset
        )
        drawRect(outlineR, outlineG, outlineB, outlineAlpha)
      }
    }
  }
  
  trait CharacterDrawer {
    def drawCharacter(c: Character, x: Int, y: Int, info: Info, vpuoffs: Int, vpvoffs: Int)
  }
  
  class CharacterSpriteDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends Drawer(a) with CharacterDrawer {
    def drawCharacter(c: Character, x: Int, y: Int, info: Info, vpuoffs: Int, vpvoffs: Int) {
      // get sprite for character
      val s = palette.sprite(c)
      val frame = if (s.animated) 0 else random(x, y) % s.frames
      
      // coordinates
      val (w, h) = c.dimensions()
      val ubl = iso2planar_u(x, y + h, 0, area.sidelength) - u0
      val ubr = iso2planar_u(x + w, y + h, 0, area.sidelength) - u0
      val vbr = iso2planar_v(x + w, y + h, 0, area.sidelength) - v0
      val ustart = ubl + tileWidth / 2
      val vstart = vbr - area.terrain(x, y).height * levelheight - s.height
      
      // draw sprite
      a.drawImage(s.image(frame), ustart, vstart, ustart + s.width, vstart + s.height, 0, 0, s.width, s.height)
    }
  }
  
  abstract class OutlineDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends Drawer(a) {
    def drawOutline(x: Int, y: Int, slotmaxheight: Int, outlineheight: Int, info: Info) {
      val hgt = outlineheight
      val vdelta = slotmaxheight * levelheight
      val (lx, ly) = info.leftXY(x, y)
      val (rx, ry) = info.rightXY(x, y)
      val (bx, by) = info.bottomXY(x, y)
      val u1 = iso2planar_u(x, y, 0, area.sidelength) - u0
      val v1 = iso2planar_v(x, y, 0, area.sidelength) - v0 - tileHeight / 4 - vdelta
      val u2 = iso2planar_u(lx, ly, 0, area.sidelength) - u0 - tileWidth / 4
      val v2 = iso2planar_v(lx, ly, 0, area.sidelength) - v0 - vdelta
      val u3 = iso2planar_u(bx, by, 0, area.sidelength) - u0
      val v3 = iso2planar_v(bx, by, 0, area.sidelength) - v0 + tileHeight / 4 - vdelta
      val u4 = iso2planar_u(rx, ry, 0, area.sidelength) - u0 + tileWidth / 4
      val v4 = iso2planar_v(rx, ry, 0, area.sidelength) - v0 - vdelta
      rect(u1, u2, u3, u4, v1 - hgt, v2 - hgt, v3 - hgt, v4 - hgt)
      drawRect(0, 200, 100, 255)
      rect(u2, u3, u3, u2, v2 - hgt, v3 - hgt, v3, v2)
      drawRect(0, 200, 100, 255)
      rect(u3, u4, u4, u3, v3 - hgt, v4 - hgt, v4, v3)
      drawRect(0, 200, 100, 255)
    }
  }
  
  class CharacterOutlineDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends OutlineDrawer(a, area, u0, v0) with CharacterDrawer {
    def drawCharacter(c: Character, x: Int, y: Int, info: Info, vpuoffs: Int, vpvoffs: Int) {
      var maxheight = 0
      c.foreachPos {
        (x, y) =>
        val h = area.terrain(x, y).height
        if (h > maxheight) maxheight = h
      }
      val s = palette.sprite(c)
      drawOutline(x, y, maxheight, s.height, info)
    }
  }
  
  trait EffectDrawer {
    def drawEffect(e: Effect, x: Int, y: Int, info: Info, vpuoffs: Int, vpvoffs: Int)
  }
  
  class EffectOutlineDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends OutlineDrawer(a, area, u0, v0) with EffectDrawer {
    def drawEffect(e: Effect, x: Int, y: Int, info: Info, vpuoffs: Int, vpvoffs: Int) {
      var maxheight = 0
      e.foreachPos {
        (x, y) =>
        val h = area.terrain(x, y).height
        if (h > maxheight) maxheight = h
      }
      drawOutline(x, y, maxheight, 100, info)
    }
  }
  
  class EffectSpriteDrawer(a: DrawAdapter, area: AreaView, u0: Int, v0: Int) extends Drawer(a) with EffectDrawer {
    def drawEffect(e: Effect, x: Int, y: Int, info: Info, vpuoffs: Int, vpvoffs: Int) {
    }
  }
  
  def iwidth: Int
  
  def iheight: Int
  
  def levelheight = 16
  
  object drawing {
    var outline: Boolean = app.render.outline
    var seethrough: Boolean = app.render.seethrough
    var indices: Boolean = app.render.indices
    var background: Boolean = app.render.background
    var shadows: Boolean = app.render.shadows
  }
  
  def pos: (Int, Int)
  
  var highlight: (Int, Int) = null;
  
  def framespersec = 35
  
  def framelength = (1000.0 / framespersec).toInt
  
  @inline final def planar2iso(u: Double, v: Double, mapsz: Int): (Double, Double) =
    (v / tileHeight + u / tileWidth - mapsz / 2, v / tileHeight - u / tileWidth + mapsz / 2);
  
  @inline final def planar2iso(u: Int, v: Int, mapsz: Int): (Double, Double) =
    planar2iso(u.toDouble, v.toDouble, mapsz)
  
  @inline final def iso2planar_u(x: Double, y: Double, z: Double, mapsz: Int): Double = (mapsz - y + x) * tileWidth / 2
  
  @inline final def iso2planar_v(x: Double, y: Double, z: Double, mapsz: Int): Double = (x + y) * tileHeight / 2 - z * levelheight
  
  @inline final def iso2planar(x: Double, y: Double, z: Double, mapsz: Int): (Double, Double) =
    (iso2planar_u(x, y, z, mapsz), iso2planar_v(x, y, z, mapsz));
  
  @inline final def iso2planar(x: Int, y: Int, z: Int, mapsz: Int): (Double, Double) =
    iso2planar(x.toDouble, y.toDouble, z.toDouble, mapsz)
  
  def tileCoord(area: AreaView, u: Int, v: Int): (Int, Int) = {
    val (x, y) = planar2iso(u + pos._1, v + pos._2, area.sidelength)
    (x.toInt, y.toInt)
  }
  
  def maxPlanarWidth(mapsz: Int) = iso2planar(mapsz, 0, 0, mapsz)._1 + tileWidth
  
  def maxPlanarHeight(mapsz: Int) = iso2planar(mapsz, mapsz, 0, mapsz)._2 + tileHeight
  
  def background(area: AreaView) = stars
  
  def redrawBackground(area: AreaView, a: DrawAdapter, ustart: Int, vstart: Int, vpuoffs: Int, vpvoffs: Int) {
    if (drawing.background) {
      val w = 800
      val h = 600
      val x0 = (pos._1 / 16  + vpuoffs) % w
      val y0 = (pos._2 / 16  + iheight - vpvoffs) % h
      for (x <- -1 to (iwidth / w + 1); y <- -1 to (iheight / h + 1))
        a.drawImage(background(area), x * w - x0, y * h - y0, x * w + w - x0, y * h + h - y0, 0, 0, w, h)
    } else {
      a.setColor(0, 0, 0, 255)
      a.fillRect(0, 0, iwidth, iheight)
    }
  }
  
  def redraw(area: AreaView, engine: Engine.State, a: DrawAdapter) {
    redrawRect(area, engine, a, pos._1, pos._2, iwidth, iheight, 0, 0)
  }
  
  protected def redrawRect(area: AreaView, engine: Engine.State, a: DrawAdapter, ustart: Int, vstart: Int, width: Int, height: Int, vpuoffs: Int, vpvoffs: Int) {
    redrawBackground(area, a, ustart, vstart, vpuoffs, vpvoffs)
    
    // determine region
    val u0 = ustart
    val v0 = vstart
    val pw = width
    val ph = height + area.maxheight() * levelheight + palette.maxSpriteHeight
    val (xtl, ytl) = planar2iso(u0, v0, area.sidelength)
    val (xtr, ytr) = planar2iso(u0 + pw, v0, area.sidelength)
    val (xbr, ybr) = planar2iso(u0 + pw, v0 + ph, area.sidelength)
    val (xbl, ybl) = planar2iso(u0, v0 + ph, area.sidelength)
    val x0 = xtl.toInt - 4
    val y0 = ytr.toInt - 1
    val w = (xbr - x0).toInt
    val h = (ybl - y0).toInt
    
    @inline def onscreen(x: Int, y: Int) = x >= x0 && y >= y0 && x < (x0 + w) && y < (y0 + h)
    object slotinfo {
      @inline def apply(x: Int, y: Int) = if (onscreen(x, y)) infos((y - y0) * w + (x - x0)) else null
      @inline def update(x: Int, y: Int, i: Info) = infos((y - y0) * w + (x - x0)) = i
    }
    object effects {
      @inline def unsetbit(x: Int, y: Int) = if (onscreen(x, y)) effectbitmap((y - y0) * w + (x - x0)) = false
      @inline def setbit(x: Int, y: Int) = if (onscreen(x, y)) effectbitmap((y - y0) * w + (x - x0)) = true
      @inline def exist(x: Int, y: Int) = if (onscreen(x, y)) effectbitmap((y - y0) * w + (x - x0)) else false
      @inline def apply(x: Int, y: Int) = if (onscreen(x, y)) effectmap((x, y)) else null
      @inline def add(x: Int, y: Int, i: Info) = {
        effectbitmap((y - y0) * w + (x - x0)) = true
        effectmap.get((x, y)) match {
          case Some(s) => s += i
          case None => effectmap.put((x, y), mutable.Set(i))
        }
      }
      @inline def remove(x: Int, y: Int, i: Info) = {
        effectmap.get((x, y)) match {
          case Some(s) =>
            s -= i
            if (s.isEmpty) {
              effectbitmap((y - y0) * w + (x - x0)) = false
              effectmap.remove((x, y))
            }
          case None =>
        }
      }
    }
    def hidden(c: Character) = hiddenCharacters(c)
    
    // 1) initialize and group infos
    if ((infos eq null) || infos.length != w * h) infos = new Array[Info](w * h)
    if ((effectbitmap eq null) || effectbitmap.length != w * h) effectbitmap = new Array[Boolean](w * h)
    for (x <- x0 until (x0 + w); y <- y0 until (y0 + h)) {
      if (area.contains(x, y)) area.characters(x, y) match {
        case NoCharacter =>
          val info = infopool.allocate()
          info.deps = deppool.allocate()
          slotinfo(x, y) = info
        case c =>
          val info = infopool.allocate()
          info.id = c.id._1 + c.id._2
          if (c.pos().equalTo(x, y)) {
            info.deps = deppool.allocate()
            info.dims = c.dimensions()
          } else info.top = c.pos()
          slotinfo(x, y) = info
      }
      
      effects.unsetbit(x, y)
    }
    for (kv <- effectmap) effects.setbit(kv._1._1, kv._1._2)
    
    // 2) compute dependencies - iterate over all the infos diagonal-wise
    def dependencies(x: Int, y: Int) {
      def regularDependencies(info: Info) {
        if ((info ne null) && info.isTop) {
          val cspr = palette.sprite(area.characters(x, y))
          val vhi = info.lowestV(x, y, area) - cspr.height
          val (u, v) = iso2planar(x, y, area.terrain(x, y).height, area.sidelength)
          val (xtop, ytop) = planar2iso(u, vhi, area.sidelength)
          val (xl, yl) = info.leftXY(x, y)
          val (xr, yr) = info.rightXY(x, y)
          val uleft = iso2planar_u(xl, yl, 0, area.sidelength) - tileWidth / 2
          val uright = iso2planar_u(xr, yr, 0, area.sidelength) + tileWidth / 2
          
          // add everything in the rectangle to the dependency list
          for (xp <- xtop.toInt to xr; yp <- ytop.toInt to yl) {
            val up = iso2planar_u(xp, yp, 0, area.sidelength)
            if (up >= uleft && up <= uright && !info.contains(x, y, xp, yp)) {
              val depinfo = slotinfo(xp, yp)
              if (depinfo ne null) info.addDep(xp, yp, depinfo)
              if (effects.exist(xp, yp)) for (e <- effects(xp, yp)) info.addEffectDep(xp, yp, e)
            }
          }
        }
      }
      def effectDependencies(info: Info) {
        if (info.isTop) for (
          xp <- info.top.x until (info.top.x + info.dims._1);
          yp <- info.top.y until (info.top.y + info.dims._2)
        ) {
          // overlapping effects
          if (effects.exist(xp, yp)) for (e <- effects(xp, yp)) {
            if (!info.sameTop(x, y, e, xp, yp))
              if (xp < x || (xp == x && (yp < y || (yp == y && info.id < e.id))))
                info.addEffectDep(xp, yp, e)
          }
          
          // underlying elements
          val slinfo = slotinfo(xp, yp)
          if (slinfo ne null) info.addDep(xp, yp, slinfo)
        }
      }
      regularDependencies(slotinfo(x, y))
      if (effects.exist(x, y)) for (effectinfo <- effects(x, y)) {
        regularDependencies(effectinfo)
        effectDependencies(effectinfo)
      }
    }
    for (i <- 0 until h; x <- 0 to i; y = i - x) dependencies(x0 + x, y0 + y)
    for (i <- 1 until h; x <- i until h; y = h - 1 + i - x) dependencies(x0 + x, y0 + y)
    
    // 3) reverse drawing
    val terraindrawer = if (drawing.outline) new TerrainOutlineDrawer(a, area, u0, v0) else new TerrainSpriteDrawer(a, area, u0, v0)
    val characterdrawer = if (drawing.outline) new CharacterOutlineDrawer(a, area, u0, v0) else new CharacterSpriteDrawer(a, area, u0, v0)
    val effectdrawer = if (drawing.outline) new EffectOutlineDrawer(a, area, u0, v0) else new EffectSpriteDrawer(a, area, u0, v0)
    import terraindrawer._
    import characterdrawer._
    import effectdrawer._
    def drawTop(x: Int, y: Int, info: Info) {
      // draw terrain and sides
      info.foreach(x, y) {
        (xp, yp) =>
        val slot = area.terrain(xp, yp)
        val u = iso2planar_u(xp, yp, slot.height, area.sidelength) - u0
        val v = iso2planar_v(xp, yp, slot.height, area.sidelength) - v0
        drawTerrain(slot, xp, yp, u, v, vpuoffs, vpvoffs)
      }
      
      // draw character
      area.characters(x, y) match {
        case NoCharacter => // do nothing
        case c => if (!hidden(c)) drawCharacter(c, x, y, info, vpuoffs, vpvoffs)
      }
    }
    def reverseDraw(x: Int, y: Int) {
      def drawInfo(info: Info) {
        if (info != null && !info.drawn) {
          if (info.isTop) {
            info.drawn = true
            info.deps.foreach((xp, yp) => reverseDraw(xp, yp))
            if (info.isEffect) drawEffect(info.effect, x, y, info, vpuoffs, vpvoffs) else drawTop(x, y, info)
          } else {
            info.drawn = true
            reverseDraw(info.top.x, info.top.y)
          }
        }
      }
      drawInfo(slotinfo(x, y))
      if (effects.exist(x, y)) for (e <- effects(x, y)) drawInfo(e)
    }
    for (i <- 0 until h; x <- 0 to i; y = i - x) reverseDraw(x0 + x, y0 + y)
    for (i <- 1 until h; x <- i until h; y = h - 1 + i - x) reverseDraw(x0 + x, y0 + y)
    
    // 4) dispose dependencies and cleanup
    for (i <- 0 until infos.length) {
      val info = infos(i)
      if (info != null) {
        infos(i) = null
        if (info.deps ne null) deppool.dispose(info.deps)
        if (info.effectdeps ne null) deppool.dispose(info.effectdeps)
        infopool.dispose(info)
      }
    }
  }
  
}






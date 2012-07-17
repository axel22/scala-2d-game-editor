/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model.components



import collection._
import mutable.Buffer



package immutable {
  
  trait Quad[+T] extends Traversable[T] {
    def default: (Int, Int) => Option[T]
    def dimensions: (Int, Int)
    def size: Int
    def apply(x: Int, y: Int): T
    def within(p: Quad.Area): Seq[(Int, Int, T)]
    def foreach(f: (Int, Int, T) => Unit): Unit
  }
  
}


class Quad[T, Acc](w: Int, h: Int, d: (Int, Int) => Option[T], compress: Boolean) extends immutable.Quad[T] with Serializable {
  private var dflt: (Int, Int) => Option[T] = d
  private var dims: (Int, Int) = (w, h);
  private var root: QNode[T] = new QEmpty[T](QNode.calcSide(dims))
  
  private def app(x: Int, y: Int) = {
    check(x, y)
    root.apply(0, 0, x, y, dflt)
  }
  
  private def check(x: Int, y: Int) = if (x < 0 || y < 0 || x >= dims._1 || y >= dims._2)
    throw new IndexOutOfBoundsException("Dims: " + dims + "; idx: " + (x, y))
  
  private def resize(ndims: (Int, Int)) = {
    dims = ndims
    root = new QEmpty[T](QNode.calcSide(dims))
  }
  
  private def upd(x: Int, y: Int, v: T) {
    check(x, y)
    root = root.update(0, 0, x, y, v, compress)
  }
  
  private def clr() {
    root = new QEmpty[T](QNode.calcSide(dims))
  }
  
  final def default = dflt
  final def dimensions = dims
  final override def size = root.elems
  final def apply(x: Int, y: Int) = app(x, y)
  final def apply(p: (Int, Int)) = app(p._1, p._2)
  final def within(p: Quad.Area): Seq[(Int, Int, T)] = {
    val b = mutable.Buffer[(Int, Int, T)]()
    root.within(0, 0, p, b)
    b
  }
  final def foreach(f: (Int, Int, T) => Unit) = root.foreach(0, 0, f)
  final def foreach[U](f: T => U): Unit = root.foreach(0, 0, (x, y, v) => f(v))
  
  final def default_=(d: (Int, Int) => Option[T])(implicit rq: Acc) = dflt = d
  final def dimensions_=(sz: (Int, Int))(implicit rq: Acc) = resize(sz)
  final def update(x: Int, y: Int, elem: T)(implicit rq: Acc) = upd(x, y, elem)
  final def remove(x: Int, y: Int)(implicit rq: Acc) = root = root.remove(0, 0, x, y, compress)
  final def clear()(implicit rq: Acc) = clr()
}


object Quad {
  sealed trait Area {
    def apply(x: Int, y: Int): Boolean
    def topy: Int
    def boty: Int
    def lefx: Int
    def rigx: Int
    def rectIn(x: Int, y: Int, len: Int): Boolean
  }
  
  def radius(x0: Int, y0: Int, r: Int) = new Area {
    val sqrr = r * r
    final def apply(xp: Int, yp: Int) = (xp - x0) * (xp - x0) + (yp - y0) * (yp - y0) <= sqrr
    final def topy = y0 - r
    final def boty = y0 + r
    final def lefx = x0 - r
    final def rigx = x0 + r
    final def rectIn(x: Int, y: Int, len: Int) = !(x > rigx || (x + len) < lefx || y > boty || (y + len) < topy)
  }
  
  final def square(x0: Int, y0: Int, r: Int) = new Area {
    final def apply(xp: Int, yp: Int) = math.abs(xp - x0) <= r && math.abs(yp - y0) <= r
    final def topy = y0 - r
    final def boty = y0 + r
    final def lefx = x0 - r
    final def rigx = x0 + r
    final def rectIn(x: Int, y: Int, len: Int) = !(x > rigx || (x + len) < lefx || y > boty || (y + len) < topy)
  }
}


private abstract class QNode[T] {
  final def lstmax = 8
  final def mxside = 4 // mxside^2 = 16
  final def forkslotside = 8
  
  def isQEmpty = false
  def isQList = false
  def side: Int
  def elems: Int
  def apply(x0: Int, y0: Int, x: Int, y: Int, d: (Int, Int) => Option[T]): T
  def within(x0: Int, y0: Int, p: Quad.Area, acc: Buffer[(Int, Int, T)]): Unit
  def update(x0: Int, y0: Int, x: Int, y: Int, v: T, c: Boolean): QNode[T]
  def remove(x0: Int, y0: Int, x: Int, y: Int, c: Boolean): QNode[T]
  def foreach(x0: Int, y0: Int, f: (Int, Int, T) => Unit): Unit
}


private object QNode {
  final def mxsz = 16
  final def calcSide(dim: (Int, Int)) = nextSideSize(dim._1 max dim._2)
  private def nextSideSize(n: Int) =
    if (n <= (1 << 2)) 1 << 2
    else if (n <= (1 << 5)) 1 << 5
    else if (n <= (1 << 8)) 1 << 8
    else if (n <= (1 << 11)) 1 << 11
    else if (n <= (1 << 14)) 1 << 14
    else if (n <= (1 << 17)) 1 << 17
    else if (n <= (1 << 20)) 1 << 20
    else if (n <= (1 << 23)) 1 << 23
    else if (n <= (1 << 26)) 1 << 26
    else if (n <= (1 << 29)) 1 << 29
    else sys.error("unsupported size")
}


private case class QEmpty[T](side: Int) extends QNode[T] {
  override def isQEmpty = true
  final def elems = 0
  final def apply(x0: Int, y0: Int, x: Int, y: Int, d: (Int, Int) => Option[T]): T = d(x, y).get
  final def within(x0: Int, y0: Int, p: Quad.Area, acc: Buffer[(Int, Int, T)]): Unit = ()
  final def update(x0: Int, y0: Int, x: Int, y: Int, v: T, c: Boolean): QNode[T] = {
    val ql = new QList[T](0, new Array[Int](4), new Array(2), side)
    ql.update(x0, y0, x, y, v, c)
  }
  final def remove(x0: Int, y0: Int, x: Int, y: Int, c: Boolean): QNode[T] = this
  final def foreach(x0: Int, y0: Int, f: (Int, Int, T) => Unit) = ()
}


private object QFork {
  val deBruijnBitPos = Array[Byte] (
    0,  1, 28,  2, 29, 14, 24,  3, 30, 22, 20, 15, 25, 17,  4,  8,
   31, 27, 13, 23, 21, 19, 16,  7, 26, 12, 18,  6, 11,  5, 10,  9
  )
}


private case class QFork[T](var elems: Int, var bmp: Long, var subs: Array[QNode[T]], side: Int)
extends QNode[T] {
  
  final def apply(x0: Int, y0: Int, x: Int, y: Int, d: (Int, Int) => Option[T]) = {
    val sofss = side >> 3 // 3 == log(forkslotside)
    val fx = (x - x0) / sofss
    val fy = (y - y0) / sofss
    val loc = (fy << 3) + fx // 3 == log(forkslotside)
    if ((bmp & (1L << loc)) != 0) {
      val pos = java.lang.Long.bitCount(bmp & ((1L << loc) - 1))
      val subx0 = x0 + fx * sofss
      val suby0 = y0 + fy * sofss
      subs(pos).apply(subx0, suby0, x, y, d)
    } else d(x, y).get
  }
  
  final def within(x0: Int, y0: Int, p: Quad.Area, buff: Buffer[(Int, Int, T)]): Unit = {
    val sofss = side >> 3
    var left = crop((p.lefx - x0) / sofss, (p.rigx - x0) / sofss, (p.topy - y0) / sofss, (p.boty - y0) / sofss, bmp)
    
    while (left != 0) {
      val singleOne = left & (-left)
      val loc = calcBitLoc(singleOne)
      val fx = loc & 7  // 7 == (1 << log(forkslotside)) - 1
      val fy = loc >> 3 // 3 == log(forkslotside)
      val subx0 = x0 + fx * sofss
      val suby0 = y0 + fy * sofss
      val pos = java.lang.Long.bitCount((singleOne - 1) & bmp)
      if (rectIn(subx0, suby0, sofss, p)) {
        subs(pos).within(subx0, suby0, p, buff)
      }
      left ^= singleOne
    }
  }
  
  private def crop(lefx: Int, rigx: Int, topy: Int, boty: Int, bmp: Long) = {
    var mask = -1L
    
    if (boty < 8) mask &= -1L >>> ((7 - boty) << 3);                     // crop bigger than boty
    if (topy > 0) mask &= -1L << (topy << 3);                            // crop smaller than topy
    if (rigx < 8) mask &= (0xff >> (7 - rigx)) * 0x0101010101010101L;    // crop bigger than rigx
    if (lefx > 0) mask &= ((0xff << lefx) & 0xff) * 0x0101010101010101L; // crop smaller than lefx
    
    bmp & mask
  }
  
  private def calcBitLoc(singleOne: Long): Int = {
    if (singleOne == 1 || ((singleOne >>> 1) < (1L << 31)))
      QFork.deBruijnBitPos((singleOne.toInt * 0x077CB531) >>> 27) // deBruijn constantP
    else
      32 + QFork.deBruijnBitPos(((singleOne >>> 32).toInt * 0x077CB531) >>> 27)
  }
  
  private def rectIn(tlx: Int, tly: Int, sidelen: Int, p: Quad.Area) = p.rectIn(tlx, tly, sidelen)
  
  final def update(x0: Int, y0: Int, x: Int, y: Int, v: T, c: Boolean) = {
    val sofss = side >> 3 // 3 == log(forkslotside)
    val fx = (x - x0) / sofss
    val fy = (y - y0) / sofss
    val loc = (fy << 3) + fx // 3 == log(forkslotside)
    val subx0 = x0 + fx * sofss
    val suby0 = y0 + fy * sofss
    if ((bmp & (1L << loc)) != 0) {
      // write("update: bmp=%x, loc=%d, fx=%d, fy=%d, sx0=%d, sy0=%d, x=%d, y=%d, x0=%d, y0=%d".format(bmp, loc, fx, fy, subx0, suby0, x, y, x0, y0))
      val pos = java.lang.Long.bitCount(bmp & ((1L << loc) - 1))
      val subnode = subs(pos)
      val oldelems = subnode.elems
      subs(pos) = subnode.update(subx0, suby0, x, y, v, c)
      
      if (subs(pos) != oldelems) elems += 1
      
      this
    } else { // create a new subnode
      val ql = new QList[T](0, new Array[Int](4), new Array(2), side / forkslotside)
      ql.update(subx0, suby0, x, y, v, c)
      val subelems = java.lang.Long.bitCount(bmp)
      val pos = java.lang.Long.bitCount(bmp & ((1L << loc) - 1))
      bmp |= (1L << loc)
      
      if (subelems < subs.length) {
        backcopy(subs, pos, subs, pos + 1, subelems - pos)
        subs(pos) = ql
      } else { // double subelems array
        val nsubs = new Array[QNode[T]](subs.length * 2)
        forwcopy(subs, 0, nsubs, 0, pos)
        nsubs(pos) = ql
        forwcopy(subs, pos, nsubs, pos + 1, subelems - pos)
        subs = nsubs
      }
      
      elems += 1
      
      this
    }
  }
  
  private def backcopy(src: Array[QNode[T]], srcpos: Int, dest: Array[QNode[T]], destpos: Int, len: Int) {
    var isrc = srcpos + len - 1
    var idest = destpos + len - 1
    while (isrc >= srcpos) {
      dest(idest) = src(isrc)
      isrc -= 1
      idest -= 1
    }
  }
  
  private def forwcopy(src: Array[QNode[T]], srcpos: Int, dest: Array[QNode[T]], destpos: Int, len: Int) {
    var isrc = srcpos
    var idest = destpos
    val until = isrc + len
    while (isrc < until) {
      dest(idest) = src(isrc)
      isrc += 1
      idest += 1
    }
  }
  
  final def remove(x0: Int, y0: Int, x: Int, y: Int, c: Boolean) = {
    val sofss = side >> 3 // 3 == log(forkslotside)
    val fx = (x - x0) / sofss
    val fy = (y - y0) / sofss
    val loc = (fy << 3) + fx // 3 == log(forkslotside)
    val subx0 = x0 + fx * sofss
    val suby0 = y0 + fy * sofss
    if ((bmp & (1L << loc)) == 0) this else {
      val subelems = java.lang.Long.bitCount(bmp)
      val pos = java.lang.Long.bitCount(bmp & ((1L << loc) - 1))
      val oldsub = subs(pos)
      val oldelems = oldsub.elems
      val newsub = oldsub.remove(subx0, suby0, x, y, c)
      
      if (!newsub.isQEmpty) {
        subs(pos) = newsub
        if (oldelems != newsub.elems) elems -= 1
      } else {
        elems -= 1
        bmp ^= (1L << loc)
        forwcopy(subs, pos + 1, subs, pos, subelems - 1 - pos)
      }
      
      if (subelems < subs.length / 4) {
        val nsubs = new Array[QNode[T]](subs.length / 2)
        forwcopy(subs, 0, nsubs, 0, subelems)
        subs = nsubs
      }
      
      if (elems > lstmax / 2) this else { // compress
        toQList(x0, y0, c)
      }
    }
  }
  
  private def toQList(x0: Int, y0: Int, c: Boolean): QList[T] = {
    val ql = new QList[T](0, new Array[Int](4), new Array[AnyRef](2), side)
    foreach(x0, y0, (x, y, v) => ql.update(x0, y0, x, y, v, c))
    ql
  }
  
  final def foreach(x0: Int, y0: Int, f: (Int, Int, T) => Unit) {
    var left = bmp
    var count = 0
    val sofss = side >> 3
    
    while (count != -1) {
      val singleOne = left & (-left)
      if (singleOne == 0) count = -1
      else {
        val loc = calcBitLoc(singleOne)
        val fx = loc & 7  // 7 == (1 << log(forkslotside)) - 1
        val fy = loc >> 3 // 3 == log(forkslotside)
        val subx0 = x0 + fx * sofss
        val suby0 = y0 + fy * sofss
        subs(count).foreach(subx0, suby0, f)
        count += 1
        left ^= singleOne
      }
    }
  }
  
}


private case class QList[T](var elems: Int, var coords: Array[Int], var lst: Array[AnyRef], side: Int)
extends QNode[T] {
  override def isQList = true
  
  final def apply(x0: Int, y0: Int, x: Int, y: Int, d: (Int, Int) => Option[T]): T = {
    var i = 0
    val until = elems * 2
    while (i < until) {
      if (x == coords(i) && y == coords(i + 1)) return lst(i / 2).asInstanceOf[T]
      i += 2
    }
    d(x, y).get
  }
  
  final def within(x0: Int, y0: Int, p: Quad.Area, buff: Buffer[(Int, Int, T)]): Unit = {
    var i = 0
    val until = elems * 2
    while (i < until) {
      val xp = coords(i)
      val yp = coords(i + 1)
      if (p(xp, yp)) buff += ((xp, yp, lst(i / 2).asInstanceOf[T]))
      i += 2
    }
  }
  
  final def update(x0: Int, y0: Int, x: Int, y: Int, v: T, c: Boolean): QNode[T] = {
    // write("list update: x0=%d, y0=%d, x=%d, y=%d".format(x0, y0, x, y))
    var i = 0
    val until = elems * 2
    while (i < until) {
      if (x == coords(i) && y == coords(i + 1)) {
        lst(i / 2) = v.asInstanceOf[AnyRef]
        return this
      }
      i += 2
    }
    
    if (elems == lstmax) {
      if (side != mxside) { // still wide
        val n = toQNode(new QFork(0, 0, new Array[QNode[T]](lstmax * 2), side), x0, y0, c)
        n.update(x0, y0, x, y, v, c)
        n
      } else { // smallest granularity
        val n = toQNode(new QMatrix(0, 0, new Array[AnyRef](QNode.mxsz)), x0, y0, c)
        n.update(x0, y0, x, y, v, c)
        n
      }
    } else {
      if (elems == lst.length) {
        val nlst = new Array[AnyRef](2 * elems)
        copy(lst, 0, nlst, 0, elems)
        lst = nlst
        val ncoords = new Array[Int](4 * elems)
        copyint(coords, 0, ncoords, 0, 2 * elems)
        coords = ncoords
      }
      
      coords(2 * elems) = x
      coords(2 * elems + 1) = y
      lst(elems) = v.asInstanceOf[AnyRef]
      elems += 1
      
      this
    }
  }
  
  private def toQNode(n: QNode[T], x0: Int, y0: Int, c: Boolean) = {
    // write("toQNode(%d, %d): ".format(x0, y0) + n + " from: " + this.lst.toList + ", " + this.coords.toList)
    var i = 0
    while (i < elems) {
      val curr = lst(i)
      n.update(x0, y0, coords(2 * i), coords(2 * i + 1), lst(i).asInstanceOf[T], c)
      i += 1
    }
    n
  }
  
  final def remove(x0: Int, y0: Int, x: Int, y: Int, c: Boolean): QNode[T] = {
    var i = 0
    val until = 2 * elems
    while (i < until) {
      if (coords(i) == x && coords(i + 1) == y) return remove(i / 2)
      i += 2
    }
    this
  }
  
  private def copy(src: Array[AnyRef], srcpos: Int, dest: Array[AnyRef], destpos: Int, len: Int) {
    var isrc = srcpos
    var idest = destpos
    val until = isrc + len
    while (isrc < until) {
      dest(idest) = src(isrc)
      isrc += 1
      idest += 1
    }
  }
  
  private def copyint(src: Array[Int], srcpos: Int, dest: Array[Int], destpos: Int, len: Int) {
    var isrc = srcpos
    var idest = destpos
    val until = isrc + len
    while (isrc < until) {
      dest(idest) = src(isrc)
      isrc += 1
      idest += 1
    }
  }
  
  private def remove(i: Int): QNode[T] = {
    elems -= 1
    
    if (elems < lst.length / 4) { // compress if necessary
      val nlst = new Array[AnyRef](lst.length / 2)
      copy(lst, 0, nlst, 0, i)
      copy(lst, i + 1, nlst, i, elems - i)
      lst = nlst
      val ncoords = new Array[Int](coords.length / 2)
      copyint(coords, 0, ncoords, 0, 2 * i)
      copyint(coords, 2 * (i + 1), ncoords, 2 * i, 2 * (elems - i))
      coords = ncoords
    } else {
      lst(i) = lst(elems)
      lst(elems) = null
      coords(2 * i) = coords(2 * elems)
      coords(2 * i + 1) = coords(2 * elems + 1)
    }
    
    if (elems > 0) this else new QEmpty[T](side)
  }
  
  final def foreach(x0: Int, y0: Int, f: (Int, Int, T) => Unit) {
    var i = 0
    while (i < elems) {
      f(coords(2 * i), coords(2 * i + 1), lst(i).asInstanceOf[T])
      i += 1
    }
  }
  
}


private case class QMatrix[T](var elems: Int, var bmp: Int, var mx: Array[AnyRef], var cval: AnyRef = null)
extends QNode[T] {
  def side = mxside
  
  final def apply(x0: Int, y0: Int, x: Int, y: Int, d: (Int, Int) => Option[T]) = if (cval ne null) cval.asInstanceOf[T] else {
    val loc = ((y - y0) << 2) | (x - x0) // 2 == log(mxside)
    if ((bmp & (1 << loc)) == 0) d(x, y).get
    else mx(loc).asInstanceOf[T]
  }
  
  final def within(x0: Int, y0: Int, p: Quad.Area, buff: Buffer[(Int, Int, T)]): Unit = if (cval eq null) {
    var loc = 0
    val until = mxside * mxside
    val b = bmp
    while (loc < until) {
      if ((b & (1 << loc)) != 0) {
        val xp = x0 + (loc & 0x3)        // 0x3 == 1 << log(mxside) - 1
        val yp = y0 + ((loc & 0xc) >> 2) // 0xc == (1 << (2 * log(mxside))) - (1 << log(mxside))
        if (p(xp, yp)) buff += ((xp, yp, mx(loc).asInstanceOf[T]))
      }
      loc += 1
    }
  } else {
    // compressed version
    var x = 0
    var y = 0
    val sd = mxside
    while (x < sd) {
      while (y < sd) {
        val xp = x0 + x
        val yp = y0 + y
        if (p(xp, yp)) buff += ((xp, yp, cval.asInstanceOf[T]))
        y += 1
      }
      y = 0
      x += 1
    }
  }
  
  private def unpack(c: Boolean) {
    // check if previously compressed and restore
    if (c && (cval ne null)) {
      val len = QNode.mxsz
      mx = new Array[AnyRef](len)
      var i = 0
      while (i < len) {
        mx(i) = cval
        i += 1
      }
      cval = null
    }
  }
  
  private def pack(c: Boolean) {
    if (c && elems == mxside * mxside) {
      var allsame = true
      val refer = mx(0)
      var i = 1
      val len = mx.length
      while (i < len) {
        if (mx(i) ne refer) {
          allsame = false
          i = len
        }
        i += 1
      }
      
      if (allsame) {
        cval = refer
        mx = null
      }
    }
  }
  
  final def update(x0: Int, y0: Int, x: Int, y: Int, v: T, c: Boolean) = {
    unpack(c)
    
    val loc = ((y - y0) << 2) | (x - x0) // 2 == log(mxside)
    if ((bmp & (1 << loc)) == 0) {
      bmp |= 1 << loc
      elems += 1
    }
    // write("update: " + loc + ", from: " + (x, y) + ", left upper: " + (x0, y0))
    mx(loc) = v.asInstanceOf[AnyRef]
    
    // check if compression needed and possible
    pack(c)
    
    this
  }
  
  final def remove(x0: Int, y0: Int, x: Int, y: Int, c: Boolean) = {
    unpack(c)
    
    val loc = ((y - y0) << 2) | (x - x0)
    val flag = 1 << loc
    if ((bmp & flag) != 0) {
      bmp ^= flag
      elems -= 1
    }
    mx(loc) = null
    
    if (elems < (lstmax / 2)) { // compress back to a list
      var ql = new QList[T](0, Array[Int](0, 0, 0, 0), new Array[AnyRef](2), side)
      var loc = 0
      val until = mxside * mxside
      val b = bmp
      while (loc < until) {
        if ((b & (1 << loc)) != 0) {
          val xp = x0 + (loc & 0x3)        // 0x3 == 1 << log(mxside) - 1
          val yp = y0 + ((loc & 0xc) >> 2) // 0xc == (1 << (2 * log(mxside))) - (1 << log(mxside))
          ql.update(x0, y0, xp, yp, mx(loc).asInstanceOf[T], c)
        }
        loc += 1
      }
      ql
    } else this
  }
  
  final def foreach(x0: Int, y0: Int, f: (Int, Int, T) => Unit) = if (cval eq null) {
    var loc = 0
    val until = mxside * mxside
    val b = bmp
    while (loc < until) {
      if ((b & (1 << loc)) != 0) {
        val xp = x0 + (loc & 0x3)        // 0x3 == 1 << log(mxside) - 1
        val yp = y0 + ((loc & 0xc) >> 2) // 0xc == (1 << (2 * log(mxside))) - (1 << log(mxside))
        f(xp, yp, mx(loc).asInstanceOf[T])
      }
      loc += 1
    }
  } else {
    // compressed version
    var x = 0
    var y = 0
    val sd = mxside
    while (x < sd) {
      while (y < sd) {
        val xp = x0 + x
        val yp = y0 + y
        f(xp, yp, cval.asInstanceOf[T])
        y += 1
      }
      y = 0
      x += 1
    }
  }
  
}









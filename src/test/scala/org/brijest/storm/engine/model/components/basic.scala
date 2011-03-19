/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine.model.components



import org.scalatest._
import org.scalatest.matchers.ShouldMatchers



class BasicTests extends WordSpec with ShouldMatchers {
  
  "Cell" should {
    
    "be read from" in {
      val c = cell(10)
      c() should equal (10)
    }
    
    "be reassigned" in {
      val c = cell(10)
      c := 7
      c() should equal (7)
    }
    
  }
  
  "Queue" should {
    
    "enqueue and dequeue an element" in {
      val q = queue[Int]
      q.enqueue(7)
      q.dequeue() should equal (7)
    }
    
    "enqueue and dequeue many elements" in {
      val many = 1000
      val q = queue[Int]
      var i = 0
      do {
        q.enqueue(i)
        i += 1
      } while (i < many)
      i = 0
      do {
        q.dequeue() should equal (i)
        i += 1
      } while (i < many)
    }
    
    "enqueue and dequeue many elements alternately" in {
      val many = 1000
      val q = queue[Int]
      var i = 0
      var sz = 0
      var last = -1
      while (i < many) {
        q.enqueue(i)
        q.front should equal (last + 1)
        
        sz += 1
        i += 1
        if (i % 3 == 1) {
          val d = q.dequeue()
          sz -= 1
          last += 1
          d should equal (last)
          q.length should equal (sz)
        }
      }
    }
    
    "iterate its elements" in {
      val many = 1000
      val q = queue[Int]
      var i = 0
      while (i < many) {
        assert(q.iterator.toSeq == (0 until i), i)
        
        q.enqueue(i)
        
        i += 1
      }
    }
  }
  
  "Table" should {
    
    "insert, lookup and remove elements or be cleared" in {
      val t = table[Int, Int]
      t.put(1, 2) should equal (None)
      t.size should equal (1)
      t(1) should equal (2)
      t.remove(1) should equal (Some(2))
      t.size should equal (0)
      t(1) = 2
      t(2) = 4
      t(1) = -2
      t.get(1) should equal (Some(-2))
      t.size should equal (2)
    }
    
    "iterate its elements" in {
      val t = table[Int, Int]
      t(1) = 2
      t(2) = 4
      t(3) = 6
      t.iterator.map(_._1).toSet should equal (Set(1, 2, 3))
    }
        
  }
  
  "Quad" should {
    
    "be created empty" in {
      quad[Int](1, 1, None).size should equal (0)
      quad[Int](2, 3, None).size should equal (0)
      quad[Int](5, 5, None).size should equal (0)
      quad[Int](9, 7, None).size should equal (0)
      quad[Int](31, 32, None).size should equal (0)
      quad[Int](260, 560, None).size should equal (0)
      quad(260, 560, Some(1)).size should equal (0)
    }

    "have one element after insertion" in {
      val q = quad[Int](10, 10, None)
      
      q(5, 5) = 7
      
      q.size should equal (1)
      q(5, 5) should equal (7)
    }
    
    "have 9 elements after insertion" in {
      val total = 9
      val q = quad[Int](10, 10, None)
      
      for (i <- 0 until total) q(i, i) = i
      
      q.size should equal (total)
      for (i <- 0 until total) q(i, i) should equal (i)
    }
    
    "have 10 elements after insertion" in {
      val q = quad[Int](10, 10, None)
      q(0, 0) = 0
      q(0, 1) = 1
      q(0, 2) = 2
      q(0, 3) = 3
      q(1, 0) = 4
      q(1, 1) = 5
      q(1, 2) = 6
      q(1, 3) = 7
      q(2, 0) = 8
      q(2, 1) = 9
      
      q.size should equal (10)
      q(0, 0) should equal (0)
      q(0, 1) should equal (1)
      q(0, 2) should equal (2)
      q(0, 3) should equal (3)
      q(1, 0) should equal (4)
      q(1, 1) should equal (5)
      q(1, 2) should equal (6)
      q(1, 3) should equal (7)
      q(2, 0) should equal (8)
      q(2, 1) should equal (9)
    }
    
    def testInsertions(d: Int) {
      val q = quad[Int](d, d, None)
      for (x <- 0 until d; y <- 0 until d) q(x, y) = x * y
      
      q.size should equal (d * d)
      for (x <- 0 until d; y <- 0 until d) assert(q(x, y) == x * y, (x, y))
    }
    
    "have 32x32 elements after insertion" in {
      testInsertions(32)
    }
    
    "have 256x256 elements after insertion" in {
      testInsertions(256)
    }
    
    "have 200k elements after insertion" in {
      val n = 200000
      val q = quad(2048, 2048, Some(-1))
      
      def convert(i: Int) = ((123 + (i >> 8)) % 2048, (i * 3) % 2048)
      
      for (i <- 0 until n) {
        val (x, y) = convert(i)
        assert(q(x, y) == -1, (i, x, y, q(x, y)))
        q(x, y) = i
      }
      
      q.size should equal (n)
      for (i <- 0 until n) {
        val (x, y) = convert(i)
        assert(q(x, y) == i, i)
      }
    }
    
    "have 5 elements within the 10 point radius" in {
      val n = 5
      val r = 10
      val sz = 256
      val (x, y) = (50, 50)
      val q = quad[Int](sz, sz, None)
      
      q(x, y) = 1
      q(x + r, y) = 1
      q(x, y + r) = 1
      q(x - r, y) = 1
      q(x + r/2, y + r/2) = 1
      
      q.within(Quad.radius(x, y, r)).length should equal (n)
      
      for (xp <- 0 until 256; yp <- 0 until 256; if (!Quad.radius(x, y, r)(xp, yp))) q(x, y) = xp * yp
      
      q.within(Quad.radius(x, y, r)).length should equal (n)
    }
    
    "have all the elements within the entire square" in {
      val sz = 256
      val q = quad[Int](sz, sz, None)
      for (x <- 0 until sz; y <- 0 until sz) q(x, y) = x * y
      q.within(Quad.square(sz / 2 - 1, sz / 2 - 1, sz / 2)).length should equal (sz * sz)
    }
    
    "have 30k elements within the entire square" in {
      val n = 30000
      val sz = 256
      val q = quad[Int](sz, sz, None)
      
      def convert(i: Int) = ((123 + (i >> 8)) % 256, (i * 3) % 256)
      
      for (i <- 0 until n; (x, y) = convert(i)) q(x, y) = i
      
      q.within(Quad.square(sz / 2 - 1, sz / 2 - 1, sz / 2)).length should equal (n)
    }
    
    "have 6 elements in the middle square" in {
      val sz = 4096
      val q = quad[Int](sz, sz, None)
      q(2000, 2000) = 1
      q(2010, 2010) = 2
      q(1950, 2005) = 3
      q(1975, 1960) = 4
      q(2040, 2045) = 5
      q(2050, 2050) = 6
      q(2051, 2051) = -1
      q(2017, 1940) = -1
      for (x <- 1100 until 1900; y <- 1900 until 2200) q(x, y) = -1
      
      val res = q.within(Quad.square(2000, 2000, 50))
      res.length should equal (6)
    }
    
    "have zero elements out of 4x4 after removal" in {
      val sz = 4
      val q = quad[Int](sz, sz, None)
      for (x <- 0 until sz; y <- 0 until sz) q(x, y) = x * y
      var count = 16
      for (x <- 0 until sz; y <- 0 until sz) {
        q.remove(x, y)
        count -= 1
        q.size should equal (count)
      }
      q.size should equal (0)
    }
    
    def testRemovals(sz: Int) = {
      val q = quad(sz, sz, Some(-1))
      for (x <- 0 until sz; y <- 0 until sz) q(x, y) = x * y
      var count = sz * sz
      for (x <- 0 until sz; y <- 0 until sz) {
        assert(q(x, y) == (x * y), (x, y))
        q.remove(x, y)
        assert(q(x, y) == (-1), (x, y))
        count -= 1
        assert(q.size == (count), (x, y))
      }
      q.size should equal (0)
    }
    
    "have zero elements out of 32x32 after removal" in {
      testRemovals(32)
    }
    
    "have zero elements out of 128x128 after removal" in {
      testRemovals(128)
    }
    
    "be cleared" in {
      val q = quad(10, 10, Some(-1))
      for (x <- 0 until 5; y <- 3 until 9) q(x, y) = x * y
      q.clear()
      q.size should equal (0)
      for (x <- 0 until 10; y <- 0 until 10) q(x, y) should equal (-1)
    }
    
    "change dimensions" in {
      val q = quad(10, 10, Some(-1))
      q.dimensions = (5, 5);
      for (x <- 0 until 5; y <- 0 until 5) q(x, y) should equal (-1)
      q.size should equal (0)
      q.dimensions should equal (5, 5)
    }
    
    "change defaults" in {
      val q = quad[Int](5, 5, None)
      q.default = (x, y) => Some(7)
      for (x <- 0 until 5; y <- 0 until 5) q(x, y) should equal (7)
    }
    
  }
  
  "Heap" should {
    
    "be enqueued and then dequeued" in {
      val h = heap[Int]
      
      h.enqueue(7)
      h.size should equal (1)
      
      for (i <- 0 until 7) {
        h.enqueue(i)
        h.size should equal (i + 2)
      }
      for (i <- 8 until 15) {
        h.enqueue(i)
        h.size should equal (i + 1)
      }
      
      for (i <- (0 until 15).reverse) {
        h.max should equal (i)
        h.dequeue() should equal (i)
      }
    }
    
    "be enqueued and dequeued with many elements" in {
      val many = 1000
      val h = heap[Int]
      
      for (i <- 0 until many) h.enqueue(i)
      
      h.size should equal (many)
      
      for (i <- (0 until many).reverse) {
        h.size should equal (i + 1)
        h.max should equal (i)
        h.dequeue() should equal (i)
      }
      
      for (i <- (0 until many).reverse) h.enqueue(i)
      
      h.size should equal (many)
      
      for (i <- (0 until many).reverse) {
        h.size should equal (i + 1)
        h.max should equal (i)
        h.dequeue() should equal (i)
      }
      
      h.clear()
      
      for (i <- util.Random.shuffle((0 until many).toSeq)) h.enqueue(i)
      
      for (i <- (0 until many).reverse) {
        h.size should equal (i + 1)
        h.max should equal (i)
        h.dequeue() should equal (i)
      }
    }
    
    "be iterated" in {
      val sq = util.Random.shuffle((0 until 1000).toSeq)
      val h = heap[Int]
      
      for (i <- sq) h.enqueue(i)
      h.size should equal (sq.size)
      val st = sq.toSet
      for (elem <- h.iterator) st(elem) should equal (true)
    }
    
  }
  
}

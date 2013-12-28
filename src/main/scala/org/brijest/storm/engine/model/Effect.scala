package org.brijest.storm.engine
package model






trait Effect {
  val id: Long
  val pos: Pos
  val dimensions: (Int, Int)
  
  @inline final def foreachPos[U](f: (Int, Int) => U) {
    var x = pos.x
    var y = pos.y
    val sx = x
    val maxx = x + dimensions._1
    val maxy = y + dimensions._2
    while (y < maxy) {
      while (x < maxx) {
        f(x, y)
        x += 1
      }
      x = sx
      y += 1
    }
  }
}

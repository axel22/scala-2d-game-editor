/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

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

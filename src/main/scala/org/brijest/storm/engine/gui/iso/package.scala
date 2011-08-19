/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui



import model._
import collection._



package object iso {
  def pngStream(name: String): java.io.InputStream = {
    getClass.getResourceAsStream("/iso/" + name + ".png")
  }
  
  def foreachDiagonally[U](x0: Int, y0: Int, w: Int, h: Int)(f: (Int, Int) => U) {
    for (i <- 0 until h; x <- 0 to i; y = i - x) f(x0 + x, y0 + y)
    for (i <- 1 until w; x <- i until w; y = h - 1 + i - x) f(x0 + x, y0 + y)
  }
}



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
  
  def confStream(name: String): java.io.InputStream = {
    getClass.getResourceAsStream("/iso/" + name + ".conf")
  }
  
  object Sprites {
    def maxheight = 320
  }
  
  def ceilpow2(n: Int) = {
    var pow2 = 1
    while (n > pow2) {
      pow2 = pow2 << 1
    }
    pow2
  }
  
}



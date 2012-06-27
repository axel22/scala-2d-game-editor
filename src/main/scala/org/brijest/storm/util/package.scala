/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import collection._



package object util {
  
  def consume[T](body: =>T)(loop: T => Boolean): Seq[T] = {
    val buffer = mutable.Buffer[T]()
    while (true) {
      println(buffer)
      val v = body
      if (!loop(v)) return buffer
      else buffer += v
    }
    sys.error("unreachable")
  }
  
}

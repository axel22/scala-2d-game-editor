/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm





package object engine {
  
  type Time = Long
  
  def zeroTime = 0L
  
  object WrapAroundTime extends Ordering[Time] {
    def compare(x: Time, y: Time) = math.signum(y - x).toInt
  }
  
  def unsupported() = throw new UnsupportedOperationException
  
  def timed(thunk: =>Unit) = {
    val t1 = System.currentTimeMillis
    thunk
    val time = System.currentTimeMillis - t1
    time
  }

  
}

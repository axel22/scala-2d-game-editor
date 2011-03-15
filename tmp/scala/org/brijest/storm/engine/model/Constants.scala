/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine






trait Constants {
  
  def framesPerSecond = 30
  def frameLength = 1000 / framesPerSecond
  def frameLengthNanos = 1000 / framesPerSecond * 1000000
  
  def turnsPerSecond = 50
  def turnLength = 1000 / turnsPerSecond
  def turnLengthNanos = 1000 / turnsPerSecond * 1000000
  
}

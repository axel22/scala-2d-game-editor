/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import model._



trait DrawAdapter {
  def setColor(r: Int, g: Int, b: Int)
  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int)
  def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
}
  

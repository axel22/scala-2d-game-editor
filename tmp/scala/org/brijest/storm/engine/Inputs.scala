/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Inputs {
  
  def getInputs(): Seq[Input]
  
}


object Mouse {
  type Button = Int
  val Left = -1
  val Middle = 0
  val Right = 1
}


sealed trait Input
case class KeyPress(chr: Char) extends Input
case class MouseClick(x: Int, y: Int, button: Mouse.Button) extends Input



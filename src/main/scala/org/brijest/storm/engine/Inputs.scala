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



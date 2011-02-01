package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Inputs {
  
  object Mouse {
    type Button = Int
    val Left = -1
    val Middle = 0
    val Right = 1
  }
  
  trait Input
  case class KeyPress(chr: Char) extends Input
  case class MouseClick(x: Int, y: Int, button: Mouse.Button) extends Input
  
  def waitInputs(nanos: Long): Seq[Input]
  
}


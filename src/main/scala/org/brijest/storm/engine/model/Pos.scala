package org.brijest.storm.engine
package model



import org.triggerspace.ImmutableValue



case class Pos(x: Int, y: Int) extends ImmutableValue {
  def to(dir: Direction) = Direction.fromTo(this, dir)
}

package org.brijest.storm.engine
package model






case class Pos(x: Int, y: Int) extends Immutable {
  def to(dir: Dir) = Dir.fromTo(this, dir)
  def adjacent(p: Pos) = math.abs(x - p.x) <= 1 && math.abs(y - p.y) <= 1
  def equalTo(x1: Int, y1: Int) = x == x1 && y == y1
  def toPair = (x, y)
}

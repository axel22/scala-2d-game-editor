package org.brijest.storm





package object engine {
  
  type Time = Long
  
  def zeroTime = 0L
  
  object WrapAroundTime extends Ordering[Time] {
    def compare(x: Time, y: Time) = math.signum(y - x).toInt
  }
  
  def interval(l1: Int, l2: Int)(x: Int) = math.max(l1, math.min(l2, x))
  
}

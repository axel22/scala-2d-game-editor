package org.brijest.storm.engine
package model



import components._



trait BasicStats {
  def delay: Int
  def heightStride: Int
}


object BasicStats {
  def default = new BasicStats {
    def delay = 20
    def heightStride = 2
  }
  
  def withDelay(i: Int) = new BasicStats {
    def delay = i
    def heightStride = 2
  }
}

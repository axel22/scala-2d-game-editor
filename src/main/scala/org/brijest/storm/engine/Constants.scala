package org.brijest.storm.engine






trait Constants {
  
  def framesPerSecond = 30
  def frameLength = 1000 / framesPerSecond
  def frameLengthNanos = 1000 / framesPerSecond * 1000000
  
  def turnsPerSecond = 50
  def turnLength = 1000 / turnsPerSecond
  def turnLengthNanos = 1000 / turnsPerSecond * 1000000
  
}

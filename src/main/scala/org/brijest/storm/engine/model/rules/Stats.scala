package org.brijest.storm
package engine.model
package rules






trait Stats extends BasicStats {
  def delay: Int
  def heightStride: Int
  def HP: Int
  def maxHP: Int
  def strength: Int
  
  def mainStats = Map(
    "HP" -> HP,
    "MaxHP" -> maxHP
  )
  def attributes = Map(
    "strength" -> strength
  )
}





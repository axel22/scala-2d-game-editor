package org.brijest.storm
package engine
package model



import collection._



trait AreaProvider extends Serializable {
  def name: String
  def acquire(): Area
  def release(a: Area): Unit
}


object AreaProvider {
  
  @SerialVersionUID(1000L)
  final class Strict(area: Area) extends AreaProvider {
    def name = "Strict"
    def acquire() = area
    def release(a: Area) {}
  }
  
}

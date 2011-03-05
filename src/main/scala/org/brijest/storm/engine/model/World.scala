package org.brijest.storm.engine.model



import org.triggerspace._



trait World {
  def name: String
  def initializeArea(id: AreaId): Area
  def initialPosition(pid: PlayerId): AreaId
  def initialPlace(pid: PlayerId, area: Area): Unit
}


object World {
  
  final class DefaultWorld extends World {
    def name = "D'Falta"
    def initializeArea(id: AreaId) = null // TODO
    def initialPosition(pid: PlayerId) = 0L
    def initialPlace(pid: PlayerId, area: Area) {
      // TODO
    }
  }
  
}

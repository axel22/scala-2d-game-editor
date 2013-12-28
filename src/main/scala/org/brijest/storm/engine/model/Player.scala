package org.brijest.storm.engine
package model



import components._



trait Player {
  def id: PlayerId
  def name: String
  def createPlayerCharacter(id: EntityId): PlayerCharacter
}


object Player {
  
  def default(pid: PlayerId) = new Player {
    def id = pid
    def name = "Default player"
    def createPlayerCharacter(id: EntityId) = new PlayerCharacter(pid, id) with rules.enroute.EnrouteRuleset
  }
  
}



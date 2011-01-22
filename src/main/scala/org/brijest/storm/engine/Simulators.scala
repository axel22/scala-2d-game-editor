package org.brijest.storm.engine



import org.triggerspace._
import model._



trait Simulators
extends Transactors
   with Constants
{
  import Simulators._
  
  /* settings */
  
  /* methods */
  
  def mainCharacterFor(pid: PlayerId): PlayerCharacter
  
  def simulatorFor(pcid: EntityId): Transactor[Info]
  
  /* simulator logic */
  
  class Simulator(t: Transactors) extends Transactor.Template[Info](t) {
    val model = struct(Info)
    import model._
    
    def transact() {
      // initialize
      initialize()
      
      repeat {
        simulationStep()
        
        resolveTriggers()
        
        notifyClients()
        
        // clear action queue
        actions.clear()
        
        // perform inter-simulator transactions
        performTransactions()
        
        // check pause state
        while (paused()) paused.await()
        
        // wait one period
        shouldStop.await(turnLengthNanos)
      } until (shouldStop())
      
      terminate()
    }
    
    def initialize() {
    }
    
    def simulationStep() {
    }
    
    def resolveTriggers() {
    }
    
    def notifyClients() {
    }
    
    def performTransactions() {
    }
    
    def terminate() {
    }
    
  }
  
}





object Simulators {
  
  case class Info(t: Transactors) extends Struct(t) {
    /* data model */
    val area = struct(Area)
    val actions = queue[(EntityId, Action)]
    val triggers = queue[Trigger]
    val transactions = queue[Transaction]
    
    /* simulation state */
    val paused = cell(false)
    val shouldStop = cell(false)
    
    /* clients */
    val clients = set[Transactor[Clients.Info]]
  }
  
}






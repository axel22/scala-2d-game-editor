package org.brijest.storm.engine



import org.triggerspace._



trait Simulators extends Transactors {
  
  def start()
  
  def stop()
  
}

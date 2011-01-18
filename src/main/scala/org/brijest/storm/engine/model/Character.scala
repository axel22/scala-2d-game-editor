package org.brijest.storm.engine
package model



import org.triggerspace._



abstract class Character(i: EntityId, m: Models) extends Entity(i, m) {
  
  val dimensions = cell((1, 1))
  
}

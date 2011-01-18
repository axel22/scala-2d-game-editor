package org.brijest.storm.engine
package model



import org.triggerspace._



case class Area(m: Models) extends Struct(m) {
  
  /* fields */
  
  val id = cell(invalidAreaId)
  
  val terrain = spatialmap(1, 1, Slot(HardRock, 0))
  
  val characters = table[Pos, Character]
  
  val items = table[Pos, List[Item]]
  
  /* methods */
  
  def entities: Iterator[Entity] = characters.iterator.map(_._2) ++ items.iterator.flatMap(_._2.iterator)
  
}





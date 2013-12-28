package org.brijest.storm
package engine
package model



import components._



trait ItemView extends EntityView {
  final def isCharacter = false
  final def isItem = true
  def weight: Int
}


abstract class Item extends Entity with ItemView {
  val id: EntityId
}


object Item {
  
  case class SimpleTestItem(id: EntityId) extends Item {
    def weight = 1
    def action(a: AreaView) = (NoAction, Sleep(1));
    def pov(a: AreaView) = a
    def chr = '~'
    def color = 0xffffff00
  }
  
}


object NoItem extends Item {
  val id = invalidEntityId
  def weight = unsupported()
  def pov(a: AreaView) = unsupported()
  def action(area: AreaView) = unsupported()
  def chr = '?'
  def color = 0xffffff00
}

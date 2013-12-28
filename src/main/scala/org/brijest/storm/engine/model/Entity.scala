package org.brijest.storm.engine
package model



import components._



trait EntityView extends Struct {
  protected implicit def m = new mutable {}

  def id: EntityId
  def action(area: AreaView): (Action, Trigger)
  def pov(area: AreaView): AreaView
  
  def chr: Char
  def color: Int
  def isCharacter: Boolean
  def isItem: Boolean
  def isRC: Boolean = false
  def isPC: Boolean = false
}


trait Entity extends EntityView with mutable

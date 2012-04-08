/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui.iso



import model._



trait Palette {
  
  def sprite(c: Character): Sprite
  
  def sprite(e: Effect): Sprite
  
  def sprite(t: Slot): Sprite
  
  def maxSpriteHeight: Int
  
}


class DefaultPalette extends Palette {
  
  class DummySprite(val width: Int, val height: Int) extends Sprite {
    def xoffset = 0
    def yoffset = 0
  }
  
  def sprite(c: Character): Sprite = c match {
    case NoCharacter => new DummySprite(0, 0)
    case c => new DummySprite(20, 50)
  }
  
  def sprite(e: Effect) = new DummySprite(30, 50)
  
  def sprite(t: Slot) = new DummySprite(48, 32)
  
  def maxSpriteHeight = Sprites.maxheight
  
}

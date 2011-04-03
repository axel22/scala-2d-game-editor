/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package impl



import org.brijest.bufferz._
import model._



class ConsoleUI(val shell: Shell with Buffers) extends UI {
  var pos = (0, 0);
  
  import shell._
  val screen = Composite(List(
    Mini(2).setText("Messages appear here. A lot of text. A lot of text. A lot of text. A lot of text... And more."),
    Canvas(MapDrawer),
    Mini().setText("Some additional text."),
    Mini().setText("Some text.")
  ))
  
  private def redraw(area: AreaView) = {
    MapDrawer.area = area
    screen.display(0, 0, width, height)
    MapDrawer.area = null
    
    shell.flush()
  }
  
  object MapDrawer extends shell.Drawer {
    var area: AreaView = null
    def draw(x0: Int, y0: Int, w: Int, h: Int) = for (x <- pos._1 until (pos._1 + w); y <- pos._2 until (pos._2 + h)) {
      val c = area.characters.locs(x, y)
      if (c != NoCharacter) shell.print(x0 + x, y0 + y, c.chr, shell.toColor(c.color))
      else {
        val items = area.items.locs(x, y)
        if (items != Nil) shell.print(x0 + x, y0 + y, items.head.chr, shell.toColor(items.head.color))
        else {
          val slot = area.terrain(x, y)
          shell.print(x0 + x, y0 + y, slot.chr, shell.toColor(slot.color))
        }
      }
    }
  }
  
  def refresh(area: AreaView) = redraw(area)
  
  def update(actions: Seq[Action], area: AreaView) = redraw(area)
}

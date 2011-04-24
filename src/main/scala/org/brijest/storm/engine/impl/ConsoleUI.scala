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



import collection._
import org.brijest.bufferz._
import model._



class ConsoleUI(val shell: Shell with Buffers) extends UI {
self =>
  var pos = (0, 0);
  private var plid = invalidPlayerId
  def playerId = synchronized { plid }
  def playerId_=(p: PlayerId) = synchronized { plid = p }
  
  /* layout */
  
  import shell._
  val messagebox = Mini(2).setText("Messages appear here. A lot of text. A lot, lot of text... And more.")
  val stats = Mini().setText("Stats appear here.")
  val conditions = Mini().setText("Miscellaneous conditions.")
  val screen = Composite(List(
    messagebox,
    Canvas(MapDrawer),
    stats,
    conditions
  ))
  
  /* drawing */
  
  private def redraw(area: AreaView) = {
    MapDrawer.area = area
    screen.display(0, 0, width, height)
    MapDrawer.area = null
    
    shell.flush()
  }
  
  object MapDrawer extends shell.Drawer {
    var area: AreaView = null
    def draw(x0: Int, y0: Int, w: Int, h: Int) = {
      val dims = area.terrain.dimensions
      for (x <- pos._1 until (pos._1 + w); y <- pos._2 until (pos._2 + h)) if (within(x, y, dims)) {
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
  }
  
  def refresh(area: AreaView) = redraw(area)
  
  def update(actions: Seq[Action], area: AreaView) = redraw(area)
  
  def message(msg: String) = messagebox.text = msg
  
  /* ui state */
  
  object uistate {
    val state = 'normal
    
    private def emitorder(o: Order) = self.synchronized {
      commands += OrderCommand(playerId, o)
    }
    
    def keyPress(chr: Char, mods: Int) = if (mods == 0) chr match {
      case 'y' => emitorder(Move(Dir.northwest))
      case 'u' => emitorder(Move(Dir.north))
      case 'i' => emitorder(Move(Dir.northeast))
      case 'h' => emitorder(Move(Dir.west))
      case 'k' => emitorder(Move(Dir.east))
      case 'n' => emitorder(Move(Dir.southwest))
      case 'm' => emitorder(Move(Dir.south))
      case ',' => emitorder(Move(Dir.southeast))
      case _ => message("Unknown command: %c".format(chr))
    }
    def mousePress(x: Int, y: Int, b: Int) {
    }
  }
  
  /* inputs */
  
  val commands = mutable.ArrayBuffer[Command]()
  
  shell.listen {
    case KeyPressed(chr, mods) => uistate.keyPress(chr, mods)
    case MousePressed(x, y, b) => uistate.mousePress(x, y, b)
    case _ =>
  }
  
  def flushCommands(): Seq[Command] = synchronized {
    val comms = commands.toList
    commands.clear()
    comms
  }
}

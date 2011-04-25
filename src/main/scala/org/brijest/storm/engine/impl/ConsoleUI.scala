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
  var engine: Option[Engine] = None
  private var plid = invalidPlayerId
  def playerId = synchronized { plid }
  def playerId_=(p: PlayerId) = synchronized { plid = p }
  
  /* layout */
  
  import shell._
  val messagebox = Mini(2).setText("Messages appear here. A lot of text. A lot, lot of text... And more.")
  val stats = Mini().setText("Stats appear here.")
  val conditions = Mini().setText("")
  val screen = Composite(List(
    messagebox,
    Canvas(MapDrawer),
    stats,
    conditions
  ))
  
  /* drawing */
  
  private def redraw(area: AreaView, s: Engine.State) = {
    uistate.center(area)
    
    MapDrawer.area = area
    screen.display(0, 0, width, height)
    MapDrawer.area = null
    
    conditions.text = conditionText(s)
    
    shell.flush()
  }
  
  object MapDrawer extends shell.Drawer {
    var area: AreaView = null
    def draw(x0: Int, y0: Int, w: Int, h: Int) = {
      val dims = area.terrain.dimensions
      for (x <- pos._1 until (pos._1 + w); y <- pos._2 until (pos._2 + h)) {
        val (xp, yp) = (x0 + x - pos._1, y0 + y - pos._2);
        if (within(x, y, dims)) {
          val c = area.characters.locs(x, y)
          if (c != NoCharacter) shell.print(xp, yp, c.chr, shell.toColor(c.color))
          else {
            val items = area.items.locs(x, y)
            if (items != Nil) shell.print(xp, yp, items.head.chr, shell.toColor(items.head.color))
            else {
              val slot = area.terrain(x, y)
              shell.print(xp, yp, slot.chr, shell.toColor(slot.color))
            }
          }
        } else shell.print(xp, yp, ' ', Color.black)
      }
    }
  }
  
  def refresh(area: AreaView, s: Engine.State) = redraw(area, s)
  
  def update(actions: Seq[Action], area: AreaView, s: Engine.State) = redraw(area, s)
  
  def message(msg: String) = messagebox.text = msg
  
  def conditionText(s: Engine.State) = {
    if (s.isPaused) "<Pause>" else "       "
  }
  
  /* ui state */
  
  object uistate {
    var state = 'normal
    var mustCenter = false
    
    def center(area: AreaView) = if (mustCenter) for (ng <- engine; pc <- area.playerCharacter(ng.player.id)) {
      pos = pc.pos().toPair - (shell.width / 2, shell.height / 2)
      mustCenter = false
    }
    
    private def emitorder(o: Order) = engine.map(_.push(OrderCommand(playerId, o)))
    
    private def emitscript(s: String) = engine.map(_.push(ScriptCommand(s)))
    
    private def emitempty() = engine.map(_.push(EmptyCommand))
    
    def keyPress(kp: KeyPressed) = if (kp.mods == 0) kp.chr match {
      case 'y' => emitorder(Move(Dir.northwest))
      case 'u' => emitorder(Move(Dir.north))
      case 'i' => emitorder(Move(Dir.northeast))
      case 'h' => emitorder(Move(Dir.west))
      case 'k' => emitorder(Move(Dir.east))
      case 'n' => emitorder(Move(Dir.southwest))
      case 'm' => emitorder(Move(Dir.south))
      case ',' => emitorder(Move(Dir.southeast))
      case ' ' => emitscript("togglePause()")
      case _ =>
        message("Unknown command: %c".format(kp.chr))
        emitempty()
    } else if (shell.withCtrl(kp.mods)) kp.chr.toInt match {
      case 25  => pos += (-1, -1); emitempty()
      case 21  => pos += (0, -1); emitempty()
      case 9   => pos += (1, -1); emitempty()
      case 8   => pos += (-1, 0); emitempty()
      case 11  => pos += (1, 0); emitempty()
      case 14  => pos += (-1, 1); emitempty()
      case 13  => pos += (0, 1); emitempty()
      case ',' => pos += (1, 1); emitempty()
      case 10 => mustCenter = true; emitempty()
      case _ =>
        message("Unknown command: C-%c [%d]".format(kp.chr, kp.chr.toInt))
        emitempty()
    }
    def mousePress(x: Int, y: Int, b: Int) {
    }
  }
  
  /* inputs */
  
  val commands = mutable.ArrayBuffer[Command]()
  
  shell.listen {
    case kp @ KeyPressed(chr, mods) => uistate.keyPress(kp)
    case MousePressed(x, y, b) => uistate.mousePress(x, y, b)
    case _ =>
  }
  
  def flushCommands(): Seq[Command] = synchronized {
    val comms = commands.toList
    commands.clear()
    comms
  }
}

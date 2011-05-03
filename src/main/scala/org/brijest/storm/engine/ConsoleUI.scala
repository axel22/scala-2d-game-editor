/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine



import collection._
import org.brijest.bufferz._
import model._



class ConsoleUI(val shell: Shell with Buffers) extends UI {
self =>
  import shell._
  
  var pos = (0, 0);
  var engine: Option[Engine] = None
  private var plid = invalidPlayerId
  def playerId = synchronized { plid }
  def playerId_=(p: PlayerId) = synchronized { plid = p }
  
  /* drawing */
  
  private def redraw(area: AreaView, s: Engine.State) = {
    uistate.center(area)
    
    MapDrawer.area = area
    uistate.screen.display(0, 0, shell.width, shell.height)
    MapDrawer.area = null
    
    attribsText(area, s).map(uistate.attribs.text = _)
    statsText(area, s).map(uistate.stats.text = _)
    uistate.conditions.text = conditionText(area, s)
    
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
  
  def message(msg: String) = uistate.messagebox.text = msg
  
  def conditionText(area: AreaView, s: Engine.State) = {
    if (s.isPaused) "<Pause>" else "       "
  }
  
  def statsText(area: AreaView, s: Engine.State) = for (ng <- engine) yield {
    val pc = area.playerCharacter(ng.player.id)
    val ms = for ((nm, stat) <- pc.stats.main) yield "%s: %s".format(nm.name, stat.niceString)
    ms mkString "  "
  }
  
  def attribsText(area: AreaView, s: Engine.State) = for (ng <- engine) yield {
    val pc = area.playerCharacter(ng.player.id)
    val att = for ((nm, stat) <- pc.stats.attributes) yield "%s: %s".format(nm.name, stat.niceString)
    att mkString " "
  }
  
  /* ui state */
  
  object uistate {
    var state = 'main
    var mustCenter = false
    var screen = mainScreen
    
    /* main layout */
    
    lazy val messagebox = Mini(2).setText("Messages appear here. A lot of text. A lot, lot of text... And more.")
    lazy val attribs = Mini().setText("")
    lazy val stats = Mini().setText("")
    lazy val conditions = Mini().setText("")
    
    private def mainScreen = Composite(List(
      messagebox,
      Canvas(MapDrawer),
      attribs,
      stats,
      conditions
    ))
    
    lazy val inventory = Listing()
    inventory.list = for (i <- 0 until 20) yield Mini().setText("  Item %d".format(i))
    
    private def inventoryScreen = Composite(List(
      Mini().setText("[[995500]]====== [[ffff00]]Inventory [[995500]]======"),
      inventory,
      messagebox
    ))
    
    def center(area: AreaView) = if (mustCenter) for (ng <- engine; pc <- area.playerCharacter(ng.player.id)) {
      pos = pc.pos().toPair - (shell.width / 2, shell.height / 2)
      mustCenter = false
    }
    
    private def emitorder(o: Order) = engine.map(_.push(OrderCommand(playerId, o)))
    
    private def emitscript(s: String) = engine.map(_.push(ScriptCommand(s)))
    
    private def emitempty() = engine.map(_.push(EmptyCommand))
    
    def keyPress(kp: KeyPressed) = state match {
      case 'main =>
        if (shell.noMods(kp.mods)) kp.chr.toInt match {
          case 55  => emitorder(Move(Dir.northwest))
          case 56  => emitorder(Move(Dir.north))
          case 57  => emitorder(Move(Dir.northeast))
          case 117 => emitorder(Move(Dir.west))
          case 111 => emitorder(Move(Dir.east))
          case 106 => emitorder(Move(Dir.southwest))
          case 107 => emitorder(Move(Dir.south))
          case 108 => emitorder(Move(Dir.southeast))
          case ' ' => emitscript("togglePause()")
          case _ =>
            message("Unknown command: %c [%d]".format(kp.chr, kp.chr.toInt))
            emitempty()
        } else if (shell.withCtrl(kp.mods) && !shell.withAlt(kp.mods)) kp.chr.toInt match {
          case _ =>
            message("Unknown command: C-%c [%d]".format(kp.chr, kp.chr.toInt))
            emitempty()
        } else if (!shell.withCtrl(kp.mods) && shell.withAlt(kp.mods)) kp.chr.toInt match {
          case 55  => pos += (-1, -1); emitempty()
          case 56  => pos += (0, -1); emitempty()
          case 57  => pos += (1, -1); emitempty()
          case 117 => pos += (-1, 0); emitempty()
          case 111 => pos += (1, 0); emitempty()
          case 106 => pos += (-1, 1); emitempty()
          case 107 => pos += (0, 1); emitempty()
          case 108 => pos += (1, 1); emitempty()
          case 105 => mustCenter = true; emitempty()
          case 101 =>
            state = 'inventory
            screen = inventoryScreen
            emitempty()
          case _ =>
            message("Unknown command: M-%c [%d]".format(kp.chr, kp.chr.toInt))
            emitempty()
        }
      case 'inventory =>
        if (shell.noMods(kp.mods)) kp.chr.toInt match {
          case 27  =>
            state = 'main
            screen = mainScreen
            emitempty()
          case _ =>
            message("Unknown command: %c [%d]".format(kp.chr, kp.chr.toInt))
            emitempty()
        }
    }
    def mousePress(x: Int, y: Int, b: Int) {
    }
  }
  
  /* inputs */
  
  val commands = mutable.ArrayBuffer[Command]()
  
  shell.listen {
    case kp @ KeyPressed(chr, mods) => uistate.keyPress(kp)
    case MousePressed(x, y, b) => uistate.mousePress(x, y, b)
    case _ => // do nothing
  }
  
  def flushCommands(): Seq[Command] = synchronized {
    val comms = commands.toList
    commands.clear()
    comms
  }
}

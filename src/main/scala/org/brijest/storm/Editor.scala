/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import org.github.scopt._
import org.eclipse.swt._
import org.eclipse.swt.custom._
import org.eclipse.swt.widgets._
import org.eclipse.swt.events._
import org.eclipse.swt.graphics._
import java.awt.image._
import javax.media.opengl._
import engine.model._
import engine.gui.iso._



object Editor {
  
  def main(args: Array[String]) {
    val config = new Config
    val parser = new EditorConfigParser(config)
    
    Initializer.default()
    
    if (parser.parse(args)) {
      startEditor(config)
    }
  }
  
  def startEditor(config: Config) {
    new Editor(config)
  }
  
}


class EditorConfigParser(config: Config) extends DefaultParser(app.editorcommand) {
  help("h", "help", "Show this help message")
  opt("w", "worldname", "The name of the world, creates one if it doesn't exist.", { v: String => config.world = Some(v)})
}


class Editor(config: Config) extends Logging {
  var glp: GLProfile = null //GLProfile.getDefault()
  var caps: GLCapabilities = null //new GLCapabilities(glp)
  
  glp = GLProfile.getDefault()
  caps = new GLCapabilities(glp)
  
  var filename: Option[String] = None
  
  def loadfrom(fn: String): World.Default = {
    import java.io._
    import java.util.zip._
    val file = new File(fn)
    if (!file.exists) null
    else {
      val fis = new FileInputStream(file)
      try {
        val zis = new ZipInputStream(fis)
        zis.getNextEntry()
        val ois = new ObjectInputStream(zis)
        ois.readObject.asInstanceOf[World.Default]
      } finally {
        fis.close()
      }
    }      
  }
  
  val world = config.world match {
    case Some(name) =>
      filename = Some(app.dir.path(app.dir.worlds) + name)
      val loaded = loadfrom(filename.get)
      if (loaded != null) loaded else new World.Default(name)
    case None =>
      new World.Default("Untitled")
  }
  
  val displ = Display.getDefault()
  val editorwindow = new editor.EditorWindow(displ)
  editorwindow.setVisible(true)
  
  displ.asyncExec(new Runnable {
    override def run() = initialize()
  })
  
  def createGLIsoUI(implicit area: Area) = {
    val areadisplay = new GLIsoUI(area, caps)
    
    /* events */
    
    var lastpress = (0, 0);
    var elevpress = (0, 0);
    var elevzeroheight = 0;
    var mode = 'none
    
    def tileAt(p: (Int, Int)) = areadisplay.tileCoord(area, p._1 - areadisplay.tileWidth / 2, p._2)
    
    def modifyTerrain(p: (Int, Int)) = {
      val (x, y) = tileAt(p)
      areadisplay.highlight = (x, y);
      
      def paintTerrain(x: Int, y: Int) {
        val selected = editorwindow.selectedTerrain
        if (selected != null && area.contains(x, y)) {
          val oslot = area.terrain(x, y)
          val nheight = if (selected == classOf[EmptySlot].getName) 0 else oslot.height
          val nslot = Slot(selected, nheight)
          area.terrain(x, y) = nslot
        }
      }
      
      def elevateTerrain(xt: Int, yt: Int) {
        if (area.contains(xt, yt)) {
          val diff = elevpress._2 - p._2
          val leveldiff = diff / areadisplay.levelheight
          val oslot = area.terrain(xt, yt)
          val nheight = if (oslot.isEmpty) 0 else math.min(math.max(0, elevzeroheight + leveldiff), area.maxHeight)
          val nslot = Slot(oslot, nheight)
          area.terrain(xt, yt) = nslot
        }
      }
      
      displ.syncExec(new Runnable {
        override def run() {
          val mults = editorwindow.brushSize.getText.split("x").map(_.toInt)
          val (xm, ym) = (mults(0), mults(1));
          def multiply(x0: Int, y0: Int)(action: (Int, Int) => Unit) {
            for (x <- x0 until (x0 + xm); y <- y0 until (y0 + ym)) action(x, y)
          }
          
          if (editorwindow.paintTerrain.getSelection) {
            multiply(x, y)(paintTerrain)
          } else if (editorwindow.elevateTerrain.getSelection) {
            val (xt, yt) = tileAt(elevpress._1, elevpress._2)
            areadisplay.highlight = (xt, yt);
            multiply(xt, yt)(elevateTerrain)
          } else if (editorwindow.insertCharacter.getSelection) {
            val selected = editorwindow.selectedChar[Character]
            if (selected != null) {
              val chr = selected.getConstructor(classOf[EntityId]).newInstance(area.newEntityId())
              chr.pos := Pos(x, y)
              if (chr.positions.forall(area.isWalkable(_))) area.characters.insert(chr)
            }
          } else if (editorwindow.removeCharacter.getSelection) {
            area.character(x, y) match {
              case NoCharacter =>
              case c: Character => area.characters.remove(c)
            }
          }
        }
      })
    }
    
    def onMiddleDrag(p: (Int, Int)) {
      areadisplay.pos = ((areadisplay.pos._1 + lastpress._1 - p._1).toInt, (areadisplay.pos._2 + lastpress._2 - p._2).toInt);
      lastpress = p
    }
    
    def onLeftDrag(p: (Int, Int)) {
      modifyTerrain(p)
      lastpress = p
    }
    
    def onMiddlePress(p: (Int, Int)) {
      lastpress = p
      mode = 'scroll
    }
    
    def onMiddleRelease(p: (Int, Int)) {
      mode = 'none
    }
    
    def onLeftPress(p: (Int, Int)) {
      lastpress = p
      elevpress = p
      elevzeroheight = area.safeTerrain(tileAt(p._1, p._2)).height
      mode = 'paint
      modifyTerrain(p)
    }
    
    def onLeftRelease(p: (Int, Int)) {
      mode = 'none
    }
    
    def onMouseMove(p: (Int, Int)) {
      areadisplay.highlight = tileAt(p)
    }
    
    /* awt events */
    
    import java.awt.event._
    
    areadisplay.addMouseListener(new MouseAdapter {
      override def mousePressed(me: MouseEvent) {
        if (me.getButton == MouseEvent.BUTTON2) onMiddlePress((me.getX, me.getY))
        if (me.getButton == MouseEvent.BUTTON1) onLeftPress((me.getX, me.getY))
      }
      override def mouseReleased(me: MouseEvent) {
        if (me.getButton == MouseEvent.BUTTON2) onMiddleRelease((me.getX, me.getY))
        if (me.getButton == MouseEvent.BUTTON1) onLeftRelease((me.getX, me.getY))
      }
    })
    
    areadisplay.addMouseMotionListener(new MouseMotionAdapter {
      override def mouseDragged(me: MouseEvent) {
        if (mode == 'scroll) onMiddleDrag((me.getX, me.getY))
        if (mode == 'paint) onLeftDrag((me.getX, me.getY))
        areadisplay.repaint()
      }
      override def mouseMoved(me: MouseEvent) {
        if (mode == 'none) {
          onMouseMove((me.getX, me.getY))
          areadisplay.repaint()
        }
      }
    })
    
    areadisplay
  }
  
  def initialize() {
    editorwindow.setImage(new graphics.Image(displ, pngStream("lightning")))
    
    import editorwindow._
    
    def loadPlaneTable() {
      planeTable.removeAll()
      for ((id, plane) <- world.planes.toSeq.sortBy(_._1)) {
        val item = new TableItem(planeTable, SWT.NONE)
        item.setText(0, id.toString)
        item.setText(1, plane.name)
        item.setText(2, plane.details)
      }
    }
    
    def loadWorldInfo() {
      worldNameLabel.setText(world.name)
      mainPlaneCombo.removeAll()
      
      val sortedplanes = world.planes.toSeq.sortBy(_._1)
      for ((id, plane) <- sortedplanes) {
        mainPlaneCombo.add(id + ": " + plane.name)
      }
      mainPlaneCombo.select(sortedplanes.indexWhere(_._1 == world.mainPlane))
      
      totalPlanesLabel.setText(sortedplanes.size.toString)
    }
    
    def save() = filename match {
      case Some(str) => saveto(str)
      case None => saveAs()
    }
    
    def saveAs() {
      val fd = new FileDialog(editorwindow, SWT.SAVE)
      fd.setText("Save")
      val fn = fd.open()
      if (fn != null) {
        filename = Some(fn)
        saveto(filename.get)
      }
    }
    
    def saveto(fn: String) {
      import java.io._
      import java.util.zip._
      val file = new File(fn)
      val fos = new FileOutputStream(file)
      try {
        val zos = new ZipOutputStream(fos)
        zos.putNextEntry(new ZipEntry("world"));
        val oos = new ObjectOutputStream(zos)
        oos.writeObject(world)
        zos.closeEntry()
      } catch {
        case e =>
          println(e)
          e.printStackTrace()
      } finally {
        fos.close()
      }
    }
    
    val saveicon = new graphics.Image(displ, pngStream("save"))
    val searchicon = new graphics.Image(displ, pngStream("magnify"))
    val painticon = new graphics.Image(displ, pngStream("paint"))
    val elevateicon = new graphics.Image(displ, pngStream("up"))
    val inserticon = new graphics.Image(displ, pngStream("insert"))
    val removeicon = new graphics.Image(displ, pngStream("remove"))
    val packageicon = new graphics.Image(displ, pngStream("package"))
    val monstericon = new graphics.Image(displ, pngStream("monster"))
    saveButton.setImage(saveicon)
    paintTerrain.setImage(painticon)
    elevateTerrain.setImage(elevateicon)
    insertCharacter.setImage(inserticon)
    removeCharacter.setImage(removeicon)
    modeToolbar.pack()
    
    /* initialize */
    def loadTerrainTable() {
      val filtertxt = terrainFilter.getText.toLowerCase
      def isFiltered(cls: Class[_]) = cls.getName.toLowerCase.indexOf(filtertxt) != -1
      
      terrainTable.removeAll()
      for (cls <- Terrain.registered; if isFiltered(cls)) {
        val inst = cls.newInstance
        val tableItem = new TableItem(terrainTable, SWT.NONE)
        val image = new Image(displ, pngStream(inst.identifier))
        tableItem.setImage(0, image)
        tableItem.setText(1, cls.getSimpleName)
        tableItem.setText(2, inst.identifier)
      }
    }
    loadTerrainTable()
    
    def resizeImage(image: Image, width: Int, height: Int) = {
      val resized = new Image(displ, image.getImageData.scaledTo(width, height))
      image.dispose()
      resized
    }
    
    def boundDimension(img: Image, maxsz: Int) = {
      val w = img.getBounds.width
      val h = img.getBounds.height
      if (w > maxsz || h > maxsz) {
        if (w > h) (maxsz, h / (w / maxsz))
        else (w / (h / maxsz), maxsz)
      } else (w, h)
    }
    
    def dummyInstance(cls: Class[Character]) = {
      cls.getConstructor(classOf[EntityId]).newInstance(invalidEntityId)
    }
    
    def characterImage(cls: Class[Character]) = {
      val inst = dummyInstance(cls)
      new Image(displ, pngStream(inst.identifier))
    }
    
    def loadCharacterTable() {
      val filtertxt = characterFilter.getText.toLowerCase
      def isFiltered(cls: Class[_]) = cls.getName.toLowerCase.indexOf(filtertxt) != -1
      
      characterTable.removeAll()
      
      import components._
      val nodes = access[free].trie[String, (Image, Class[Character])]
      val treeRoot = new TreeItem(characterTable, SWT.NONE)
      treeRoot.setText("Characters")
      treeRoot.setImage(monstericon)
      
      for (cls <- CharacterSet.registered; if isFiltered(cls)) {
        val origimage = characterImage(cls)
        val (w, h) = boundDimension(origimage, 18)
        val image = resizeImage(origimage, w, h)
        val path = cls.getName.split("\\.")
        nodes(path) = (image, cls)
      }
      
      def insertChildren(ti: TreeItem, n: nodes.Node) {
        for ((k, child) <- n.children) insert(ti, child, Nil)
      }
      def insert(parent: TreeItem, n: nodes.Node, prefix: Seq[String]) {
        def onlyChild = n.children.iterator.next._2
        if (n.children.size == 1 && onlyChild.children.size > 0)
          insert(parent, onlyChild, prefix :+ n.prefix.last)
        else {
          val item = new TreeItem(parent, SWT.NONE)
          n.value match {
            case Some((img, cls)) =>
              item.setText(cls.getSimpleName)
              item.setImage(img)
              item.setData(cls)
            case None =>
              item.setText((prefix :+ n.prefix.last).mkString("."))
              item.setImage(packageicon)
          }
          insertChildren(item, n)
          item.setExpanded(true)
        }
      }
      insertChildren(treeRoot, nodes.tree)
      treeRoot.setExpanded(true)
    }
    loadCharacterTable()
    
    def createCharacterTip(p: Point) {
      val item = characterTable.getItem(p)
      if (item != null && item.getData != null) {
        if (characterTip != null) {
          characterTip.dispose()
          characterTip = null
        }
        characterTip = new editor.CharacterTip(displ)
        val cls = item.getData.asInstanceOf[Class[Character]]
        val origimage = characterImage(cls)
        val (w, h) = boundDimension(origimage, 128)
        val image = resizeImage(origimage, w, h)
        characterTip.imageLabel.setImage(image)
        characterTip.nameLabel.setText(cls.getSimpleName)
        characterTip.dimensionLabel.setText("Size: " + dummyInstance(cls).dimensions())
        characterTip.setVisible(true)
      }
    }
    
    eventHandler = new editor.EditorEventHandler {
      def openArea() {
        val selection = planeTable.getSelection
        if (selection.nonEmpty) {
          val id = selection.head.getText(0).toInt
          val plane = world.plane(id)
          
          val (x, y) = if (plane.size == 1) (0, 0) else {
            val chooser = new editor.XYChooser(editorwindow, SWT.APPLICATION_MODAL)
            chooser.width = plane.size - 1
            chooser.height = plane.size - 1
            val coord = chooser.open()
            if (coord == null) return
            (coord.x, coord.y);
          }
          val areaid = areaId(id, x, y)
          val area = world.area(areaid) match {
            case Some(a: AreaProvider.Strict) => a.acquire()
            case Some(a) =>
              val messageBox = new MessageBox(editorwindow, SWT.ICON_WARNING | SWT.OK)
              messageBox.setText("Unknown provider")
              messageBox.setMessage("Unable to handle area provider type: " + a.name + ".")
              messageBox.open()
              return
            case None =>
              val messageBox = new MessageBox(editorwindow, SWT.ICON_WARNING | SWT.OK)
              messageBox.setText("Unknown area")
              messageBox.setMessage("There is no area with id: " + areaid + " (plane: " + id + ", x: " + x + ", y: " + y + ").")
              messageBox.open()
              return
          }
          
          val tbtmMap = new CTabItem(leftTabs, SWT.CLOSE);
          tbtmMap.setText("Area: " + world.plane(id).get.name + " at (" + x + ", " + y + ")");
          leftTabs.setSelection(tbtmMap)
          
          areaPanel = new editor.AreaPanel(leftTabs, SWT.NONE)
          tbtmMap.setControl(areaPanel)
          tbtmMap.setData(area)
          
          val canvasPane = createGLIsoUI(area)
          
          areaPanel.areaCanvasPane.add(canvasPane)
        }
      }
      
      def event(name: String, arg: Object): Unit = (name, arg) match {
        case ("Fast open area", _) =>
          openArea()
        case ("Open area", e: SelectionEvent) =>
          openArea()
        case ("Resize area", _) =>
          val selection = leftTabs.getSelection
          selection.getData match {
            case area: Area =>
              implicit val _ = area
              val chooser = new editor.XYChooser(editorwindow, SWT.APPLICATION_MODAL)
              chooser.width = 1024
              chooser.height = 1024
              chooser.xinit = area.width
              chooser.yinit = area.height
              val coord = chooser.open()
              if (coord == null) return
              area.resize(coord.x, coord.y)
            case _ =>
              val messageBox = new MessageBox(editorwindow, SWT.ICON_WARNING | SWT.OK)
              messageBox.setText("Select area")
              messageBox.setMessage("Must select a tab with an area.")
              messageBox.open()
          }
        case ("Set default terrain", _) =>
          val selection = leftTabs.getSelection
          selection.getData match {
            case area: Area =>
              implicit val _ = area
              val chooser = new editor.TerrainChooser(editorwindow, SWT.APPLICATION_MODAL)
              chooser.terrains = Terrain.registeredNames.toArray
              val name = chooser.open()
              if (name == null) return
              Terrain.forName(name) match {
                case Some(cls) =>
                  val slot = Some(Slot(cls, 0))
                  area.terrain.default = (x, y) => slot
                case None =>
              }
            case _ =>
              val messageBox = new MessageBox(editorwindow, SWT.ICON_WARNING | SWT.OK)
              messageBox.setText("Select area")
              messageBox.setMessage("Must select a tab with an area.")
              messageBox.open()
          }
        case ("Remove plane", _) =>
          val selection = planeTable.getSelection
          if (selection.nonEmpty) {
            val id = selection.head.getText(0).toInt
            val messageBox = new MessageBox(editorwindow, SWT.ICON_WARNING | SWT.YES | SWT.NO)
            messageBox.setText("Remove plane")
            messageBox.setMessage("Are you sure you want to remove the plane?")
            val response = messageBox.open();
            if (response == SWT.YES) {
              world.planes.remove(id)
              loadPlaneTable()
            }
          }
        case ("Add plane", e: SelectionEvent) =>
          val creator = new editor.PlaneCreatorDialog(editorwindow, SWT.APPLICATION_MODAL)
          val info = creator.open()
          if (info == null) return
          
          val plane = new world.DefaultPlane(info(0).toString, info(1).asInstanceOf[Int])
          val id = world.newPlaneId()
          world.planes(id) = plane
          
          for (x <- 0 until plane.size; y <- 0 until plane.size) {
            info(2) match {
              case "Strict" => world.areas(areaId(id, x, y)) = new AreaProvider.Strict(Area.emptyDungeon(32, 32))
            }
          }
          
          loadPlaneTable()
          loadWorldInfo()
        case ("World name", _) =>
          world.name = worldNameLabel.getText
        case ("Main plane", _) =>
          val sel = mainPlaneCombo.getSelectionIndex
          if (sel != -1) {
            world.mainPlane = sel
          }
        case ("Terrain filter", _) =>
          loadTerrainTable()
        case ("Character filter", _) =>
          loadCharacterTable()
        case ("Character hover", p: Point) =>
          createCharacterTip(p)
        case ("Save", _) =>
          save()
        case ("Save as", _) =>
          saveAs()
      }
    }
    
    loadPlaneTable()
    loadWorldInfo()
  }
  
  try {
    editorwindow.open()
    editorwindow.layout()
    while (!editorwindow.isDisposed()) {
      if (!displ.readAndDispatch()) {
	displ.sleep()
      }
    }
  } catch {
    case e => logger.error("exception in event dispatch thread: " + e)
  }
  
}














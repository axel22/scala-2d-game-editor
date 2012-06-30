/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package gui.iso



import collection._
import swing._
import java.awt.image._
import java.lang.ref.SoftReference
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.media.opengl._
import javax.media.opengl.glu.GLU
import com.sun.opengl.util.GLUT
import org.brijest.storm.engine.model._



class GLIsoUI(val name: String) extends IsoUI with GLPaletteCanvas {
  
  class AreaDisplay extends GLCanvas(caps)
  
  var resizestamp = 0L
  val caps = new GLCapabilities()
  var buffer = new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR)
  val areadisplay = new AreaDisplay
  val frame = new Frame {
    title = name
    peer.add(areadisplay)
    areadisplay.requestFocus()
  }
  
  areadisplay.addGLEventListener(new GLEventListener {
    def display(drawable: GLAutoDrawable) {
      val gl = drawable.getGL()
      
      if (cachedarea != null) {
        redraw(cachedarea, null, new GLAutoDrawableDrawAdapter(drawable))
      }
    }
    
    def init(drawable: GLAutoDrawable) {
      resizestamp += 1
      initShadowMap(drawable)
    }
    
    def displayChanged(drawable: GLAutoDrawable, mode: Boolean, device: Boolean) {
    }
    
    def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
      resizestamp += 1
    }
  })
  
  frame.size = new Dimension(640, 480)
  frame.peer.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  frame.open()
  
  /* implementations */
  
  def width: Int = frame.size.width
  
  def height: Int = frame.size.height
  
  def refresh(area: AreaView, state: Engine.State) = this.synchronized {
    cachedarea = area
    areadisplay.repaint()
    
    // val glad = new GLAutoDrawableDrawAdapter(areadisplay)
    
    // val t = timed {
    //   redraw(area, state, glad)
    // }
    
    //println("Time to render: %d ms".format(t))
  }
  
  var cachedarea: AreaView = null
  
  val palette = new DefaultGLPalette
  
  /* shadows */
  
  val SHADOW_TEX_SIZE = 1024
  var pbuffer: GLPbuffer = null
  
  private def initShadowMap(drawable: GLAutoDrawable) {
    if (pbuffer != null) {
      pbuffer.destroy()
      pbuffer = null
    }
    pbuffer = GLDrawableFactory.getFactory.createGLPbuffer(caps, null, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE, drawable.getContext)
  }
  
  private def drawScene(area: AreaView, engine: Engine.State, a: DrawAdapter, gl: GL) {
    import gl._
    import GL._
    
    def drawCube(x: Int, y: Int) {
      val slot = area.terrain(x, y)
      val hgt = slot.height
      if (hgt == 0) return
      
      glBegin(GL_QUADS)
      
      glVertex3f(x - 0.5f, y - 0.5f, hgt)
      glVertex3f(x - 0.5f, y + 0.5f, hgt)
      glVertex3f(x + 0.5f, y + 0.5f, hgt)
      glVertex3f(x + 0.5f, y - 0.5f, hgt)
      
      // glVertex3f(x - 0.5f, y - 0.5f, hgt)
      // glVertex3f(x - 0.5f, y + 0.5f, hgt)
      // glVertex3f(x + 0.5f, y + 0.5f, hgt)
      // glVertex3f(x + 0.5f, y - 0.5f, hgt)
      
      glEnd()
    }
    
    var x = 0
    var y = 0
    while (y < area.terrain.dimensions._2) {
      while (x < area.terrain.dimensions._1) {
        drawCube(x, y)
        x += 1
      }
      y += 1
      x = 0
    }
  }
  
  override def redraw(area: AreaView, engine: Engine.State, a: DrawAdapter) {
    super.redraw(area, engine, a)
    val gl = a.asInstanceOf[GLAutoDrawableDrawAdapter].gl
    val glu = new GLU
    import gl._
    import GL._
    
    glClear(GL_DEPTH_BUFFER_BIT)
    glEnable(GL_DEPTH_TEST)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    //glOrtho(0.0f, areadisplay.getWidth, areadisplay.getHeight, 0.0f, 0.0f, 1.0f)
    val wdt = areadisplay.getWidth / 60
    val hgt = -areadisplay.getHeight / 60
    glOrtho(-wdt, wdt, -hgt, hgt, -30, 100.0)
    
    // val widthHeightRatio = areadisplay.getWidth.toFloat / areadisplay.getHeight
    //gluOrtho2D(-(float)w/h, (float)w/h, -1.0, 1.0);
    // glu.gluPerspective(45, widthHeightRatio, 1, 1000)
    glu.gluLookAt(40.f, 40.f, 30.f, 0, 0, 0, 0, 0, 1)
    
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    
    drawScene(area, engine, a, gl)
  }
  
  class GLAutoDrawableDrawAdapter(drawable: GLAutoDrawable) extends DrawAdapter {
    val gl = drawable.getGL
    val glut = new GLUT
    import gl._
    import GL._
    
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, drawable.getWidth, drawable.getHeight, 0, 0, 1)
    glMatrixMode(GL_MODELVIEW)
    glDisable(GL_DEPTH_TEST)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    
    def drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
      // TODO fix
      glBegin(GL_LINES)
      glVertex3f(x1, y1, 0)
      glVertex3f(x2, y2, 0)
      glEnd()
    }
    
    def setColor(r: Int, g: Int, b: Int) {
      glColor3ub(r.toByte, g.toByte, b.toByte)
    }
    
    def drawString(s: String, x: Int, y: Int) {
      // TODO fix
      glRasterPos2i(x, y);
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, s)
    }
    
    def setFontSize(sz: Float) {
      // TODO
    }
    
    def drawPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int) {
      glBegin(GL_LINES)
      var i = 0
      while (i < n) {
        val k = (i + 1) % n
        glVertex3i(xpoints(i), ypoints(i), 0)
        glVertex3i(xpoints(k), ypoints(k), 0)
        i += 1
      }
      glEnd()
    }
    
    def fillPoly(xpoints: Array[Int], ypoints: Array[Int], n: Int) {
      glBegin(GL_POLYGON)
      var i = 0
      while (i < n) {
        glVertex3i(xpoints(i), ypoints(i), 0)
        i += 1
      }
      glEnd()
    }
    
    def drawImage(image: Img, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int) {
      val w = image.width
      val h = image.height
      
      image.cache(resizestamp, gl)
      
      val left = 100
      val top = 100
      
      glEnable(GL_TEXTURE_2D)
      
      glBindTexture(GL_TEXTURE_2D, image.texno)
      
      glBegin(GL_POLYGON)
      
      val tx1 = 1.0 * sx1 / image.width
      val ty1 = 1.0 * sy1 / image.height
      val tx2 = 1.0 * sx2 / image.width
      val ty2 = 1.0 * sy2 / image.height
      
      glTexCoord2d(tx1, ty1)
      glVertex2d(dx1, dy1)
      glTexCoord2d(tx2, ty1)
      glVertex2d(dx2, dy1)
      glTexCoord2d(tx2, ty2)
      glVertex2d(dx2, dy2)
      glTexCoord2d(tx1, ty2)
      glVertex2d(dx1, dy2)
      
      glEnd()
      
      glDisable(GL_TEXTURE_2D)
    }
    
    def fillRect(x1: Int, y1: Int, w: Int, h: Int) {
      // TODO
    }
    
  }
  
}



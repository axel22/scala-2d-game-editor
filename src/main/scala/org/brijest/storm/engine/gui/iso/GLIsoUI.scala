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
import com.sun.opengl.util.GLUT
import org.brijest.storm.engine.model._



class GLIsoUI(val name: String) extends IsoUI with GLPaletteCanvas {
  
  class AreaDisplay extends GLCanvas(caps)
  
  var timesResized = 0L
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
      
      // copy the drawing buffer
      if (cachedarea != null) {
        redraw(cachedarea, null, new GLAutoDrawableDrawAdapter(drawable))
      }
    }

    def init(drawable: GLAutoDrawable) {
      timesResized += 1
    }

    def displayChanged(drawable: GLAutoDrawable, mode: Boolean, device: Boolean) {
    }

    def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
      timesResized += 1
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
    gl.glEnable(GL.GL_BLEND)
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)
    
    def drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
      glBegin(GL_LINES)
      glVertex3f(x1, y1, 0)
      glVertex3f(x2, y2, 0)
      glEnd()
    }
    def setColor(r: Int, g: Int, b: Int) {
      glColor3ub(r.toByte, g.toByte, b.toByte)
    }
    def drawString(s: String, x: Int, y: Int) {
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
      
      image.cache(timesResized, drawable.getGL)
      
      val left = 100
      val top = 100
      
      glEnable(GL.GL_TEXTURE_2D)
      
      glBindTexture(GL.GL_TEXTURE_2D, image.texno)
      glBegin(GL.GL_POLYGON)
      
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
    }
    def fillRect(x1: Int, y1: Int, w: Int, h: Int) {
      // TODO
    }
  }


  
}



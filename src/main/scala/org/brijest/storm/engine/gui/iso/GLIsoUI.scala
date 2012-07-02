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
  
  def width: Int = areadisplay.getWidth
  
  def height: Int = areadisplay.getHeight
  
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
  
  val SHADOW_TEX_SIZE = 512
  val shadowtexno = new Array[Int](1)
  val lightprojmatrix = new Array[Float](16)
  val lightviewmatrix = new Array[Float](16)
  val camprojmatrix = new Array[Float](16)
  val camviewmatrix = new Array[Float](16)
  val shadowtexmatrix = new Array[Float](16)
  val biasmatrix = Array[Float](
    0.5f, 0.f, 0.f, 0.f,
    0.f, 0.5f, 0.f, 0.f,
    0.f, 0.f, 0.5f, 0.f,
    0.5f, 0.5f, 0.5f, 1.f
  )
  lazy val debugscreen = new Array[Byte](width * height * 4)
  
  private def initShadowMap(drawable: GLAutoDrawable) {
    val gl = drawable.getGL()
    import gl._
    import GL._
    
    glGenTextures(1, shadowtexno, 0)
    glBindTexture(GL_TEXTURE_2D, shadowtexno(0))
    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE, 0,
                 GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, null)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP)
  }
  
  override def redraw(area: AreaView, engine: Engine.State, a: DrawAdapter) {
    val gl = a.asInstanceOf[GLAutoDrawableDrawAdapter].gl
    val glu = new GLU
    import gl._
    import GL._
    
    val (u0, v0) = pos
    val pw = width
    val ph = height + area.maxheight() * levelheight + palette.maxSpriteHeight
    val (xtl, ytl) = planar2iso(u0, v0, area.sidelength)
    val (xtr, ytr) = planar2iso(u0 + pw, v0, area.sidelength)
    val (xbr, ybr) = planar2iso(u0 + pw, v0 + ph, area.sidelength)
    val (xbl, ybl) = planar2iso(u0, v0 + ph, area.sidelength)
    val xmid = (xtl + xbr) / 2
    val ymid = (ytr + ybl) / 2
    val xfrom = interval(0, area.width)(xtl.toInt)
    val xuntil = interval(0, area.width)(xbr.toInt)
    val yfrom = interval(0, area.height)(ytr.toInt)
    val yuntil = interval(0, area.height)(ybl.toInt)
    val xlook = xmid - 14.35
    val ylook = ymid - 13.35
    
    def drawScene() {
      def drawCube(x: Int, y: Int) {
        val slot = area.terrain(x, y)
        val hgt = slot.height * 0.55f
        
        glBegin(GL_QUADS)
        
        /* top */
        
        glVertex3d(x - 0.5f, y - 0.5f, hgt)
        glVertex3d(x - 0.5f, y + 0.5f, hgt)
        glVertex3d(x + 0.5f, y + 0.5f, hgt)
        glVertex3d(x + 0.5f, y - 0.5f, hgt)
        
        /* sides */
        
        if (hgt > 0) {
          glVertex3f(x - 0.5f, y + 0.5f, hgt)
          glVertex3f(x - 0.5f, y + 0.5f, 0)
          glVertex3f(x - 0.5f, y - 0.5f, 0)
          glVertex3f(x - 0.5f, y - 0.5f, hgt)
          
          glVertex3f(x - 0.5f, y - 0.5f, hgt)
          glVertex3f(x - 0.5f, y - 0.5f, 0)
          glVertex3f(x + 0.5f, y - 0.5f, 0)
          glVertex3f(x + 0.5f, y - 0.5f, hgt)
          
          glVertex3f(x + 0.5f, y - 0.5f, hgt)
          glVertex3f(x + 0.5f, y - 0.5f, 0)
          glVertex3f(x + 0.5f, y + 0.5f, 0)
          glVertex3f(x + 0.5f, y + 0.5f, hgt)
          
          glVertex3f(x + 0.5f, y + 0.5f, hgt)
          glVertex3f(x + 0.5f, y + 0.5f, 0)
          glVertex3f(x - 0.5f, y + 0.5f, 0)
          glVertex3f(x - 0.5f, y + 0.5f, hgt)
        }
        
        glEnd()
      }
      
      var x = xfrom
      var y = yfrom
      while (y < yuntil) {
        while (x < xuntil) {
          drawCube(x, y)
          x += 1
        }
        y += 1
        x = 0
      }
    }
    
    glPushMatrix()
    
    /* calc matrices */
    
    val lightpos = (-100.f, 100.f, 65.f);
    
    def initLightMatrices() {
      glLoadIdentity()
      val wdt = width / 45
      val hgt = height / 45
      glOrtho(wdt, -wdt, -hgt, hgt, -300.0, 900.0)
      glGetFloatv(GL_MODELVIEW_MATRIX, lightprojmatrix, 0)
      
      glLoadIdentity()
      glu.gluLookAt(
        xlook + lightpos._1, ylook + lightpos._2, lightpos._3,
        xlook, ylook, 0.0f,
        0.f, 0.f, 1.0f)
      glGetFloatv(GL_MODELVIEW_MATRIX, lightviewmatrix, 0)
    }
    
    def initCamMatrices() {
      glLoadIdentity()
      val wdt = width / (tileWidth * math.sqrt(2))
      val hgt = height / (tileHeight * math.sqrt(2) * 2)
      glOrtho(wdt, -wdt, -hgt, hgt, -300.0, 900.0)
      glGetFloatv(GL_MODELVIEW_MATRIX, camprojmatrix, 0)
      
      glLoadIdentity()
      val xyside = 100.f
      val zcenter = xyside * math.sqrt(2) / math.sqrt(3)
      glu.gluLookAt(
        xlook + xyside, ylook + xyside, zcenter,
        xlook, ylook, 0,
        0.f, 0.f, 1.f)
      glGetFloatv(GL_MODELVIEW_MATRIX, camviewmatrix, 0)
    }
    
    initLightMatrices()
    initCamMatrices()
    
    def lightView() {
      glMatrixMode(GL_PROJECTION)
      glLoadMatrixf(lightprojmatrix, 0)
      
      glMatrixMode(GL_MODELVIEW)
      glLoadMatrixf(lightviewmatrix, 0)
    }
    
    def orthoView() {
      glMatrixMode(GL_PROJECTION)
      glLoadMatrixf(camprojmatrix, 0)
      
      glMatrixMode(GL_MODELVIEW)
      glLoadMatrixf(camviewmatrix, 0)
    }
    
    /* draw scene from light point of view and copy to the texture buffer */
    
    glViewport(0, 0, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE)
    lightView()
    
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glEnable(GL_DEPTH_TEST)
    
    drawScene()
    
    glBindTexture(GL_TEXTURE_2D, shadowtexno(0))
    glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE)
    
    /* 2d draw scene */
    
    glPopMatrix()
    glViewport(0, 0, width, height)
    
    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, width, height, 0, 0, 1)
    glMatrixMode(GL_MODELVIEW)
    glDisable(GL_DEPTH_TEST)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    
    glReadPixels(0, 0, width, height, GL_DEPTH_COMPONENT, GL_FLOAT, java.nio.ByteBuffer.wrap(debugscreen));
    //super.redraw(area, engine, a)
    glDrawPixels(width, height, GL_LUMINANCE, GL_FLOAT, java.nio.ByteBuffer.wrap(debugscreen));
    
    /* draw scene with shadows from camera point of view */
    
    def calcTextureMatrix() {
      glPushMatrix()
      
      glLoadIdentity()
      glLoadMatrixf(biasmatrix, 0)
      glMultMatrixf(lightprojmatrix, 0)
      glMultMatrixf(lightviewmatrix, 0)
      glGetFloatv(GL_MODELVIEW_MATRIX, shadowtexmatrix, 0)
      
      glPopMatrix()
    }
    
    calcTextureMatrix()
    
    glPushMatrix()
    orthoView()
    
    glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR)
    glTexGenfv(GL_S, GL_EYE_PLANE, shadowtexmatrix, 0)
    glEnable(GL_TEXTURE_GEN_S)
    
    glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR)
    glTexGenfv(GL_T, GL_EYE_PLANE, shadowtexmatrix, 3)
    glEnable(GL_TEXTURE_GEN_T)
    
    glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR)
    glTexGenfv(GL_R, GL_EYE_PLANE, shadowtexmatrix, 6)
    glEnable(GL_TEXTURE_GEN_R)
    
    glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR)
    glTexGenfv(GL_Q, GL_EYE_PLANE, shadowtexmatrix, 9)
    glEnable(GL_TEXTURE_GEN_Q)
    
    glBindTexture(GL_TEXTURE_2D, shadowtexno(0))
    glEnable(GL_TEXTURE_2D)
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE_ARB, GL_COMPARE_R_TO_TEXTURE)
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC_ARB, GL_LEQUAL)
    
    glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE_ARB, GL_INTENSITY)
    
    glAlphaFunc(GL_GEQUAL, 0.99f)
    glEnable(GL_ALPHA_TEST)
    
    //drawScene()
    
    glDisable(GL_TEXTURE_2D)
    
    glDisable(GL_TEXTURE_GEN_S)
    glDisable(GL_TEXTURE_GEN_T)
    glDisable(GL_TEXTURE_GEN_R)
    glDisable(GL_TEXTURE_GEN_Q)
    
    glDisable(GL_LIGHTING)
    glDisable(GL_ALPHA_TEST)
    
    /* reset */
    
    glPopMatrix()
  }
  
  class GLAutoDrawableDrawAdapter(val drawable: GLAutoDrawable) extends DrawAdapter {
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



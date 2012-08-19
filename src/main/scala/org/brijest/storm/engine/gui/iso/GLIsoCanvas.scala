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
import org.apache.commons.io.IOUtils
import java.awt.image._
import java.lang.ref.SoftReference
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.media.opengl._
import javax.media.opengl.awt.GLCanvas
import javax.media.opengl.glu.GLU
import GL._
import GL2._
import GL2ES1._
import GL2ES2._
import fixedfunc.GLLightingFunc._
import fixedfunc.GLMatrixFunc._
import org.brijest.storm.engine.model._
import scalagl._



class GLIsoCanvas(val area: Area, val caps: GLCapabilities)
extends GLCanvas(caps) with IsoCanvas with UI with GLPaletteCanvas with Logging {
self =>
  
  var resizestamp = 0L
  
  this.addGLEventListener(new GLEventListener {
    def display(drawable: GLAutoDrawable) {
      val gl = drawable.getGL().getGL2()
      
      redraw(area, null, new GLAutoDrawableDrawAdapter(drawable))
    }
    
    def init(drawable: GLAutoDrawable) {
      resizestamp += 1
      initialize(drawable)
    }
    
    def dispose(drawable: GLAutoDrawable) {
    }
    
    def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
      resizestamp += 1
    }
  })
  
  /* implementations */
  
  def iwidth: Int = this.getWidth
  
  def iheight: Int = this.getHeight
  
  val palette = new GLPalette
  
  /* shadows */
  
  val SHADOW_TEX_SIZE = 2048
  var shadowtexno: Int = -1
  var shadowfbo: Int = -1
  var shadowdrb: Int = -1
  val LITE_TEX_SIZE = 1024
  var litetexno: Int = -1
  var litefbo: Int = -1
  val orthoshadowProgram = ShaderProgram()
  val lightProgram = ShaderProgram()
  lazy val debugscreen = new Array[Byte](1680 * 1050 * 4)
  
  private def initialize(drawable: GLAutoDrawable) {
    implicit val gl = drawable.getGL().getGL2()
    val index = new Array[Int](1)
    import gl._
    
    glGenTextures(1, index, 0)
    shadowtexno = index(0)
    glBindTexture(GL_TEXTURE_2D, shadowtexno)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL)
    glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE, 0,
                 GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null)
    
    glGenFramebuffers(1, index, 0)
    shadowfbo = index(0)
    
    glGenRenderbuffers(1, index, 0)
    shadowdrb = index(0)
    
    glGenTextures(1, index, 0)
    litetexno = index(0)
    glBindTexture(GL_TEXTURE_2D, litetexno)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL)
    glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, LITE_TEX_SIZE, LITE_TEX_SIZE, 0,
                 GL_RGB, GL_UNSIGNED_INT, null)
    
    glGenFramebuffers(1, index, 0)
    litefbo = index(0)
    
    glEnable(GL_NORMALIZE)
    
    glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE)
    glEnable(GL_COLOR_MATERIAL)
    glMaterialfv(GL_FRONT, GL_SPECULAR, Array[Float](1.f, 1.f, 1.f, 1.f), 0)
    glMaterialf(GL_FRONT, GL_SHININESS, 16.f)
    
    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    
    /* shaders */
    
    def createShaderProgram(name: String, proghandle: ShaderProgram) {
      var program = -1
      val vs = glCreateShader(GL_VERTEX_SHADER)
      val fs = glCreateShader(GL_FRAGMENT_SHADER)
      val vsprogis = this.getClass.getClassLoader.getResourceAsStream("shaders/%s.vert".format(name))
      val fsprogis = this.getClass.getClassLoader.getResourceAsStream("shaders/%s.frag".format(name))
      import JavaConverters._
      val vsrc = IOUtils.readLines(vsprogis).asScala.mkString("\n")
      val fsrc = IOUtils.readLines(fsprogis).asScala.mkString("\n")
      IOUtils.closeQuietly(vsprogis)
      IOUtils.closeQuietly(fsprogis)
      
      proghandle.acquire()

      try {
        proghandle.vertex.attach("vertex", vsrc)
        proghandle.fragment.attach("fragment", fsrc)
      } catch {
        case e => logger.error(e.getMessage)
      }
    }
    
    createShaderProgram("orthoshadow", orthoshadowProgram)
    createShaderProgram("blurlight", lightProgram)
  }
  
  override def redraw(area: AreaView, engine: Engine.State, a: DrawAdapter) {
    val t = timed {
      redrawInternal(area, engine, a)
    }
    
    //logger.info("redrawn in " + t + " ms")
  }

  private def redrawInternal(area: AreaView, engine: Engine.State, a: DrawAdapter) {
    a.asInstanceOf[GLAutoDrawableDrawAdapter].gl.glClear(GL_COLOR_BUFFER_BIT)
    
    val (wrect, hrect) = if (drawing.shadows) (1680, 1050) else (iwidth, iheight)
    
    var u = 0
    var v = 0
    while (v < iheight) {
      val hgt = if (iheight - v < hrect) iheight - v else hrect
      while (u < iwidth) {
        val wdt = if (iwidth - u < wrect) iwidth - u else wrect
        redrawRect(area, engine, a, pos._1 + u, pos._2 + v, wdt, hgt, u, iheight - hgt - v)
        u += wdt
      }
      u = 0
      v += hgt
    }
  }
  
  protected override def redrawRect(area: AreaView, engine: Engine.State, a: DrawAdapter, ustart: Int, vstart: Int, width: Int, height: Int, vpuoffs: Int, vpvoffs: Int) {
    implicit val gl = a.asInstanceOf[GLAutoDrawableDrawAdapter].gl
    val glu = new GLU
    import gl._
    
    val u0 = ustart
    val v0 = vstart
    val pw = width
    val ph = height + area.maxHeight * levelheight + palette.maxSpriteHeight
    val (xtl, ytl) = planar2iso(u0, v0, area.sidelength)
    val (xtr, ytr) = planar2iso(u0 + pw, v0, area.sidelength)
    val (xbr, ybr) = planar2iso(u0 + pw, v0 + ph, area.sidelength)
    val (xbl, ybl) = planar2iso(u0, v0 + ph, area.sidelength)
    val xmid = (xtl + xbr) / 2.0
    val ymid = (ytr + ybl) / 2.0
    val xfrom = interval(0, area.width)(xtl.toInt - 4)
    val xuntil = interval(0, area.width)(xbr.toInt)
    val yfrom = interval(0, area.height)(ytr.toInt - 1)
    val yuntil = interval(0, area.height)(ybl.toInt)
    val xlook = xmid - 14.50
    val ylook = ymid - 13.50
    
    type Vec3 = (Float, Float, Float);
    
    trait Light {
      def shader: ShaderProgram
      def color: Vec3
    }
    
    case class OrthoLight(pos: Vec3, color: Vec3) extends Light {
      def shader = orthoshadowProgram
    }
    
    def drawScene(withCharacters: Boolean) {
      def drawCube(x: Int, y: Int, span: Float, bottom: Float, top: Float) = geometry(GL_QUADS) {
        /* top */
        v3d(x - span, y - span, top)
        v3d(x - span, y + span, top)
        v3d(x + span, y + span, top)
        v3d(x + span, y - span, top)

        /* sides */
        if (top > 0) {
          v3d(x - span, y - span, top)
          v3d(x - span, y - span, bottom)
          v3d(x - span, y + span, bottom)
          v3d(x - span, y + span, top)

          v3d(x + span, y - span, top)
          v3d(x + span, y - span, bottom)
          v3d(x - span, y - span, bottom)
          v3d(x - span, y - span, top)

          v3d(x + span, y + span, top)
          v3d(x + span, y + span, bottom)
          v3d(x + span, y - span, bottom)
          v3d(x + span, y - span, top)

          v3d(x - span, y + span, top)
          v3d(x - span, y + span, bottom)
          v3d(x + span, y + span, bottom)
          v3d(x + span, y + span, top)
        }
      }
      
      var x = xfrom
      var y = yfrom
      while (y < yuntil) {
        while (x < xuntil) {
          val slot = area.terrain(x, y)
          slot match {
            case slot: EmptySlot =>
            case slot => drawCube(x, y, 0.5f, 0.f, slot.height * 0.275f)
          }
          if (withCharacters) area.character(x, y) match {
            case NoCharacter =>
            case chr if chr.pos().x == x && chr.pos().y == y =>
              val (w, h) = chr.dimensions()
              val sprite = palette.sprite(chr)
              // TODO adjust for multi slot characters
              //drawCube(x + w / 2, y + h / 2, sprite.width / 32.f, slot.height * 0.55f, slot.height * 0.55f + sprite.height / 16.f)
            case _ =>
          }
          x += 1
        }
        y += 1
        x = 0
      }
    }
    
    def sendUniform1i(program: Int, varname: String, v: Int) {
      val loc = glGetUniformLocation(program, varname)
      if (loc == -1) {
        logger.warn("could not send uniform: " + varname)
      }
      glUniform1i(loc, v)
    }
    
    def sendUniform3f(program: Int, varname: String, x: Float, y: Float, z: Float) {
      val loc = glGetUniformLocation(program, varname)
      if (loc == -1) {
        logger.warn("could not send uniform: " + varname)
      }
      glUniform3f(loc, x, y, z)
    }
    
    def debugTexture(texno: Int) {
      glPushMatrix()
      glEnable(GL_TEXTURE_2D)
      glEnable(GL_DEPTH_TEST)
      glColor4f(1.0f,1.0f,1.0f,1.0f)
      glEnable(GL_BLEND)
      glBlendFunc(GL_ALPHA,GL_ONE_MINUS_SRC_ALPHA)
      
      glMatrixMode(GL_PROJECTION)
      glLoadIdentity()
      glOrtho(0, width, height, 0, 0, 1)
      glMatrixMode(GL_MODELVIEW)
      glDisable(GL_DEPTH_TEST)
      glEnable(GL_BLEND)
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      
      glMatrixMode(GL_TEXTURE)
      glLoadIdentity()
      
      glBindTexture(GL_TEXTURE_2D, texno)
      
      glBegin(GL_QUADS);
      glTexCoord2f(0, 1); glVertex2d(0, 0);
      glTexCoord2f(1, 1); glVertex2d(width / 4, 0);
      glTexCoord2f(1, 0); glVertex2d(width / 4, height / 4);
      glTexCoord2f(0, 0); glVertex2d(0, height / 4);
      glEnd();
      
      glDisable(GL_DEPTH_TEST)
      glDisable(GL_TEXTURE_2D)
      glPopMatrix()
    }
    
    /* calc matrices */
    
    val mainlightpos = (-40.f, 100.f, 70.f);
    
    def renderLightLayer(light: Light) {
      val xyside = 100.f
      val zcenter = xyside * math.sqrt(2) / math.sqrt(3)
      val campos = (xyside, xyside, zcenter.toFloat);
      val shader = light.shader
      val (lightProjMatrix, lightViewMatrix) = light match {
        case OrthoLight(lightpos, _) =>
          val pm = matrices.orthoProjection(1050 / 14, 1050 / 14, -600.0, 600.0)
          val vm = matrices.orthoView(
            xlook + 60.f + lightpos._1, ylook - 50.f + lightpos._2, lightpos._3,
            xlook + 60.f, ylook - 50.f, 0.f,
            0.f, 0.f, 1.f)
          (pm, vm)
      }
      val camProjMatrix = {
        val wdt = width / (tileWidth * math.sqrt(2))
        val hgt = height / (tileHeight * math.sqrt(2) * 2)
        matrices.orthoProjection(wdt, hgt, -300.0, 900.0)
      }
      val camViewMatrix = matrices.orthoView(
        xlook + campos._1, ylook + campos._2, campos._3,
        xlook, ylook, 0.f,
        0.f, 0.f, 1.f)
      
      /* draw scene from light point of view and copy to the texture buffer */
      
      glViewport(0, 0, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE)
      glEnable(GL_CULL_FACE)
      glCullFace(GL_FRONT)
      
      matrices(lightProjMatrix, lightViewMatrix) {
        //lightView()
        glDisable(GL_CULL_FACE)

        glColor4f(1.f, 1.f, 1.f, 0.f)
        glEnable(GL_DEPTH_TEST)

        //glBindFramebuffer(GL_FRAMEBUFFER, shadowfbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, shadowtexno, 0)

        glClear(GL_DEPTH_BUFFER_BIT)

        drawScene(true)

        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        glBindTexture(GL_TEXTURE_2D, shadowtexno)
        if (drawing.shadows) glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, SHADOW_TEX_SIZE, SHADOW_TEX_SIZE)
        //debugTexture(shadowtexno)
        //return
      }

      def debugReadScreen() {
        glReadPixels(0, 0, width, height, GL_DEPTH_COMPONENT, GL_FLOAT, java.nio.ByteBuffer.wrap(debugscreen));
      }
      
      def debugScreen() {
        glDrawPixels(width, height, GL_LUMINANCE, GL_FLOAT, java.nio.ByteBuffer.wrap(debugscreen));
      }
      
      /* render scene with shadows from camera point of view */
      
      glViewport(0, 0, width, height)

      def initglsl() {
        glActiveTexture(GL_TEXTURE0)
        glEnable(GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, shadowtexno)
        
        glEnable(GL_DEPTH_TEST)
        glClear(GL_DEPTH_BUFFER_BIT)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
      }
      
      val depTexMatrix = (lightProjMatrix * lightViewMatrix).to[TextureMatrix]
      
      matrices(depTexMatrix, camProjMatrix, camViewMatrix) {
        initglsl()

        glViewport(0, 0, LITE_TEX_SIZE, LITE_TEX_SIZE)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)

        glBindFramebuffer(GL_FRAMEBUFFER, litefbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, litetexno, 0)

        using.program(shader) {
          shader.uniform("shadowtex") << 0
          shader.uniform("light_color") << light.color
        
          drawScene(false)
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        glDisable(GL_CULL_FACE)

        glMatrixMode(GL_TEXTURE)
        glLoadIdentity()

        glMatrixMode(GL_MODELVIEW)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_NONE)
        glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_NONE)

        glDisable(GL_TEXTURE_2D)

        glDisable(GL_TEXTURE_GEN_S)
        glDisable(GL_TEXTURE_GEN_T)
        glDisable(GL_TEXTURE_GEN_R)
        glDisable(GL_TEXTURE_GEN_Q)

        glDisable(GL_ALPHA_TEST)
      }
    }
    
    /* first clear texture */
    
    if (drawing.shadows) {
      glBindFramebuffer(GL_FRAMEBUFFER, litefbo)
      glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, litetexno, 0)
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      glBindFramebuffer(GL_FRAMEBUFFER, 0)
      
      renderLightLayer(OrthoLight(mainlightpos, (0.3f, 0.3f, 0.3f)))
    }
    
    //glBindTexture(GL_TEXTURE_2D, litetexno)
    //glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, LITE_TEX_SIZE, LITE_TEX_SIZE)
    
    glViewport(vpuoffs, vpvoffs, width, height)
    
    /* 2d render scene */
    
    def renderScene() {
      //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      glMatrixMode(GL_PROJECTION)
      glLoadIdentity()
      glOrtho(0, width, height, 0, 0, 1)
      glMatrixMode(GL_MODELVIEW)
      glLoadIdentity()
      glDisable(GL_DEPTH_TEST)
      glDisable(GL_CULL_FACE)
      glEnable(GL_BLEND)
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      
      super.redrawRect(area, engine, a, ustart, vstart, width, height, vpuoffs, vpvoffs)
      
      glEnable(GL_DEPTH_TEST)
    }
    
    def blurLightLayer() {
      glDisable(GL_DEPTH_TEST)
      glEnable(GL_TEXTURE_2D)
      
      glMatrixMode(GL_TEXTURE)
      glLoadIdentity()
      
      glMatrixMode(GL_PROJECTION)
      glLoadIdentity()
      glOrtho(0, width, height, 0, 0, 1)
      glMatrixMode(GL_MODELVIEW)
      
      glBindTexture(GL_TEXTURE_2D, litetexno)
      
      using.program(lightProgram) {
        lightProgram.uniform("litetex") << 0

        glBegin(GL_QUADS)
        glTexCoord2f(0, 1); glVertex2d(0, 0)
        glTexCoord2f(1, 1); glVertex2d(width, 0)
        glTexCoord2f(1, 0); glVertex2d(width, height)
        glTexCoord2f(0, 0); glVertex2d(0, height)
        glEnd()

      }

      glEnable(GL_DEPTH_TEST)
      glDisable(GL_TEXTURE_2D)
    }
    
    renderScene()
    if (drawing.shadows) blurLightLayer()
  }
  
  class GLAutoDrawableDrawAdapter(val drawable: GLAutoDrawable) extends DrawAdapter {
    val gl = drawable.getGL().getGL2()
    import gl._
    import GL._
    
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, drawable.getWidth, drawable.getHeight, 0, 0, 1)
    glMatrixMode(GL_MODELVIEW)
    glDisable(GL_DEPTH_TEST)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glDisable(GL_CULL_FACE)
    
    def setLineWidth(w: Float) {
      glLineWidth(w.toFloat)
    }
    
    def drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
      // TODO fix
      glBegin(GL_LINES)
      glVertex3f(x1, y1, 0)
      glVertex3f(x2, y2, 0)
      glEnd()
    }
    
    def setColor(r: Int, g: Int, b: Int, alpha: Int) {
      glColor4ub(r.toByte, g.toByte, b.toByte, alpha.toByte)
    }
    
    def drawString(s: String, x: Int, y: Int) {
      // TODO fix
      glRasterPos2i(x, y)
      //glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, s)
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



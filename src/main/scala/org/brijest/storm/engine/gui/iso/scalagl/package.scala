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
import javax.media.opengl._
import GL._
import GL2._
import GL2ES1._
import GL2ES2._
import fixedfunc.GLMatrixFunc._
import javax.media.opengl.glu.GLU



package object scalagl {

  private val result = new Array[Int](4)

  private[scalagl] val glu = new GLU

  /* geometry */

  @inline def geometry[T](geomtype: Int)(block: =>T)(implicit gl: GL2): T = {
    import gl._
    glBegin(geomtype)
    try block
    finally glEnd()
  }

  @inline def v3f(x: Float, y: Float, z: Float)(implicit gl: GL2) {
    gl.glVertex3f(x, y, z)
  }

  @inline def v3d(x: Double, y: Double, z: Double)(implicit gl: GL2) {
    gl.glVertex3d(x, y, z)
  }

  @inline def v2d(x: Double, y: Double)(implicit gl: GL2) {
    gl.glVertex2d(x, y)
  }

  @inline def tc2f(x: Float, y: Float)(implicit gl: GL2) {
    gl.glTexCoord2f(x, y)
  }

  /* contexts */

  abstract class Setup {
    protected def set(): Unit
    protected def unset(): Unit

    def foreach(f: Null => Unit) {
      set()
      try f(null)
      finally unset()
    }
  }

  object graphics {
    def clear(bits: Int)(implicit gl: GL2) {
      gl.glClear(bits)
    }
  }

  object enabling {

    def apply(settings: Int*)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var filtered: Seq[Int] = _
      def set() {
        filtered = settings.filter(!glIsEnabled(_))
        for (s <- filtered) glEnable(s)
      }
      def unset() {
        for (s <- filtered) glDisable(s)
      }
    }

  }

  object disabling {

    def apply(settings: Int*)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var filtered: Seq[Int] = _
      def set() {
        filtered = settings.filter(glIsEnabled(_))
        for (s <- filtered) glDisable(s)
      }
      def unset() {
        for (s <- filtered) glEnable(s)
      }
    }

  }

  object setting {

    def color(r: Float, g: Float, b: Float, a: Float)(implicit gl: GL2): Setup = new Setup {
      import gl._
      val color4f = new Array[Float](4)
      def set() {
        glGetFloatv(GL_CURRENT_COLOR, color4f, 0)
        glColor4f(r, g, b, a)
      }
      def unset() {
        glColor4f(
          color4f(0),
          color4f(1),
          color4f(2),
          color4f(3)
        )
      }
    }

    def cullFace(v: Int)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var ov: Int = -1
      def set() {
        glGetIntegerv(GL_CULL_FACE_MODE, result, 0)
        ov = result(0)
        glCullFace(v)
      }
      def unset() {
        glCullFace(ov)
      }
    }

    def viewport(x: Int, y: Int, wdt: Int, hgt: Int)(implicit gl: GL2): Setup = new Setup {
      import gl._
      val result = new Array[Int](4)
      def set() {
        glGetIntegerv(GL_VIEWPORT, result, 0)
        glViewport(x, y, wdt, hgt)
      }
      def unset() {
        glViewport(
          result(0),
          result(1),
          result(2),
          result(3)
        )
      }
    }

    def blendFunc(sfactor: Int, dfactor: Int)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var osrc: Int = _
      var odst: Int = _
      def set() {
        glGetIntegerv(GL_BLEND_SRC, result, 0)
        osrc = result(0)
        glGetIntegerv(GL_BLEND_DST, result, 0)
        odst = result(0)
        glBlendFunc(sfactor, dfactor)
      }
      def unset() {
        glBlendFunc(osrc, odst)
      }
    }
  }

  object using {

    def program(h: ShaderProgram)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var oldprogram: Int = _
      def set() {
        glGetIntegerv(GL_CURRENT_PROGRAM, result, 0)
        oldprogram = result(0)
        glUseProgram(h.pindex)
      }
      def unset() {
        glUseProgram(oldprogram)
      }
    }

    def texture(t: Texture)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var oldbinding: Int = _
      def set() {
        glGetIntegerv(t.binding, result, 0)
        oldbinding = result(0)
        glBindTexture(t.target, t.index)
      }
      def unset() {
        glBindTexture(t.target, oldbinding)
      }
    }

    def framebuffer(fb: FrameBuffer)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var oldbinding: Int = _
      def set() {
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, result, 0)
        oldbinding = result(0)
        glBindFramebuffer(GL_FRAMEBUFFER, fb.index)
      }
      def unset() {
        glBindFramebuffer(GL_FRAMEBUFFER, oldbinding)
      }
    }

    def matrix[T](ms: Matrix*)(implicit gl: GL2): Setup = new Setup {
      import gl._
      var oldmode: Int = _
      def set() {
        glGetIntegerv(GL_MATRIX_MODE, result, 0)
        val oldmode = result(0)
        for (m <- ms) {
          glMatrixMode(m.mode)
          glPushMatrix()
          glLoadMatrixd(m.array, 0)
        }
      }
      def unset() {
        for (m <- ms.reverse) {
          glMatrixMode(m.mode)
          glPopMatrix()
        }
        glMatrixMode(oldmode)
      }
    }

  }

}



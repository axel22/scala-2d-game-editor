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

  abstract class Setup[T] {
    def foreach[U](f: T => U): Unit
  }

  object graphics {
    def clear(bits: Int)(implicit gl: GL2) {
      gl.glClear(bits)
    }
  }

  object enabling {

    def apply(settings: Int*)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        var filtered = settings.filter(!glIsEnabled(_))
        for (s <- filtered) glEnable(s)
        try f(null)
        finally for (s <- filtered) glDisable(s)
      }
    }

  }

  object disabling {

    def apply(settings: Int*)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        var filtered = settings.filter(glIsEnabled(_))
        for (s <- filtered) glDisable(s)
        try f(null)
        finally for (s <- filtered) glEnable(s)
      }
    }

  }

  object setting {

    def color(r: Float, g: Float, b: Float, a: Float)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      val color4f = new Array[Float](4)
      def foreach[U](f: Null => U) {
        glGetFloatv(GL_CURRENT_COLOR, color4f, 0)
        glColor4f(r, g, b, a)
        try f(null)
        finally glColor4f(
          color4f(0),
          color4f(1),
          color4f(2),
          color4f(3)
        )
      }
    }

    def cullFace(v: Int)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        glGetIntegerv(GL_CULL_FACE_MODE, result, 0)
        var ov: Int = result(0)
        ov = result(0)
        glCullFace(v)
        try f(null)
        finally glCullFace(ov)
      }
    }

    def viewport(x: Int, y: Int, wdt: Int, hgt: Int)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      val result = new Array[Int](4)
      def foreach[U](f: Null => U) {
        glGetIntegerv(GL_VIEWPORT, result, 0)
        glViewport(x, y, wdt, hgt)
        try f(null)
        finally glViewport(
          result(0),
          result(1),
          result(2),
          result(3)
        )
      }
    }

    def blendFunc(sfactor: Int, dfactor: Int)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        glGetIntegerv(GL_BLEND_SRC, result, 0)
        val osrc = result(0)
        glGetIntegerv(GL_BLEND_DST, result, 0)
        val odst = result(0)
        glBlendFunc(sfactor, dfactor)
        try f(null)
        finally glBlendFunc(osrc, odst)
      }
    }
  }

  object using {

    def program(h: ShaderProgram)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        glGetIntegerv(GL_CURRENT_PROGRAM, result, 0)
        val oldprogram = result(0)
        glUseProgram(h.pindex)
        try f(null)
        finally glUseProgram(oldprogram)
      }
    }

    def texture(t: Texture)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        glGetIntegerv(t.binding, result, 0)
        val oldbinding = result(0)
        glBindTexture(t.target, t.index)
        try f(null)
        finally glBindTexture(t.target, oldbinding)
      }
    }

    def framebuffer(fb: FrameBuffer)(implicit gl: GL2): Setup[fb.binding.type] = new Setup[fb.binding.type] {
      import gl._
      def foreach[U](f: fb.binding.type => U) {
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, result, 0)
        val oldbinding = result(0)
        glBindFramebuffer(GL_FRAMEBUFFER, fb.index)
        try f(fb.binding)
        finally glBindFramebuffer(GL_FRAMEBUFFER, oldbinding)
      }
    }

    def matrix[T](ms: Matrix*)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      import gl._
      def foreach[U](f: Null => U) {
        glGetIntegerv(GL_MATRIX_MODE, result, 0)
        val oldmode = result(0)
        for (m <- ms) {
          glMatrixMode(m.mode)
          glPushMatrix()
          glLoadMatrixd(m.array, 0)
        }
        try f(null)
        finally {
          for (m <- ms.reverse) {
            glMatrixMode(m.mode)
            glPopMatrix()
          }
          glMatrixMode(oldmode)
        }
      }
    }

  }

}



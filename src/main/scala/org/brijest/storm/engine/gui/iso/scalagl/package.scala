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

  object graphics {
    def clear(bits: Int)(implicit gl: GL2) {
      gl.glClear(bits)
    }
  }

  object enabling {

    def apply(settings: Int*)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      val filtered = settings.filter(!glIsEnabled(_))
      for (s <- filtered) glEnable(s)
      try block
      finally {
        for (s <- filtered) glDisable(s)
      }
    }

  }

  object disabling {

    def apply(settings: Int*)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      val filtered = settings.filter(glIsEnabled(_))
      for (s <- filtered) glDisable(s)
      try block
      finally {
        for (s <- filtered) glEnable(s)
      }
    }

  }

  object setting {
    private val color4f = new Array[Float](4)
    private val color4i = new Array[Int](4)

    def color(r: Float, g: Float, b: Float, a: Float)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetFloatv(GL_CURRENT_COLOR, color4f, 0)
      val or = color4f(0)
      val og = color4f(1)
      val ob = color4f(2)
      val oa = color4f(3)
      glColor4f(r, g, b, a)
      try block
      finally glColor4f(or, og, ob, oa)
    }

    def cullFace(v: Int)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(GL_CULL_FACE_MODE, result, 0)
      val ov = result(0)
      glCullFace(v)
      try block
      finally glCullFace(ov)
    }

    def viewport(x: Int, y: Int, wdt: Int, hgt: Int)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(GL_VIEWPORT, result, 0)
      val ox = result(0)
      val oy = result(1)
      val ow = result(2)
      val oh = result(3)
      glViewport(x, y, wdt, hgt)
      try block
      finally glViewport(ox, oy, ow, oh)
    }

    def blendFunc(sfactor: Int, dfactor: Int)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(GL_BLEND_SRC, result, 0)
      val osrc = result(0)
      glGetIntegerv(GL_BLEND_DST, result, 0)
      val odst = result(0)
      glBlendFunc(sfactor, dfactor)
      try block
      finally glBlendFunc(osrc, odst)
    }
  }

  object using {

    def program(h: ShaderProgram)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(GL_CURRENT_PROGRAM, result, 0)
      val oldprogram = result(0)
      try {
        glUseProgram(h.pindex)
        block
        glUseProgram(0)
      } finally glUseProgram(oldprogram)
    }

    def texture(t: Texture)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(t.binding, result, 0)
      val oldbinding = result(0)
      glBindTexture(t.target, t.index)
      try {
        block
      } finally {
        glBindTexture(t.target, oldbinding)
      }
    }

    def framebuffer(fb: FrameBuffer)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(GL_FRAMEBUFFER_BINDING, result, 0)
      val oldbinding = result(0)
      glBindFramebuffer(GL_FRAMEBUFFER, fb.index)
      try {
        block
      } finally {
        glBindFramebuffer(GL_FRAMEBUFFER, oldbinding)
      }
    }

    def matrix[T](ms: Matrix*)(block: =>T)(implicit gl: GL2): T = {
      import gl._

      glGetIntegerv(GL_MATRIX_MODE, result, 0)
      val oldmode = result(0)
      glPushMatrix()
      for (m <- ms) {
        glMatrixMode(m.mode)
        glPushMatrix()
        glLoadMatrixd(m.array, 0)
      }
      try {
        block
      } finally {
        for (m <- ms.reverse) {
          glMatrixMode(m.mode)
          glPopMatrix()
        }
        glMatrixMode(oldmode)
        glPopMatrix()
      }
    }

  }

}



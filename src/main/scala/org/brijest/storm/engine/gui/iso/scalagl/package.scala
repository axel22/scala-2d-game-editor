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

  val result = Array(1)

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

  /* contexts */

  object using {

    @inline def program(h: ShaderProgram)(block: =>Unit)(implicit gl: GL2) {
      import gl._
      glGetIntegerv(GL_CURRENT_PROGRAM, result, 0)
      val oldprogram = result(0)
      try {
        glUseProgram(h.pindex)
        block
        glUseProgram(0)
      } finally glUseProgram(oldprogram)
    }

    @inline def texture(t: Texture)(block: =>Unit)(implicit gl: GL2) {
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

    @inline def framebuffer(fb: FrameBuffer)(block: =>Unit)(implicit gl: GL2) {
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

    @inline def matrix[T](ms: Matrix*)(block: =>T)(implicit gl: GL2): T = {
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



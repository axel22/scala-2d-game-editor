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



package object opengl {

  private val glu = new GLU

  /* geometry */

  @inline def geometry[T](geomtype: Int)(block: =>T)(implicit gl: GL2): T = {
    import gl._
    glBegin(geomtype)
    try block
    finally glEnd()
  }

  @inline def v(x: Float, y: Float, z: Float)(implicit gl: GL2) {
    gl.glVertex3f(x, y, z)
  }

  @inline def v(x: Double, y: Double, z: Double)(implicit gl: GL2) {
    gl.glVertex3d(x, y, z)
  }
  
  /* transformation matrices */

  class Matrix(arr: Array[Double]) {
    final val array: AnyRef = arr
  }

  final class ProjectionMatrix(a: Array[Double]) extends Matrix(a)

  final class ModelviewMatrix(a: Array[Double]) extends Matrix(a)

  object Matrix {

    val identity = new Matrix(Array(
      1.0, 0.0, 0.0, 0.0,
      0.0, 1.0, 0.0, 0.0,
      0.0, 0.0, 1.0, 0.0,
      0.0, 0.0, 0.0, 1.0
      ))

    def orthoProjection(wdt: Double, hgt: Double, nearPlane: Double, farPlane: Double)(implicit gl: GL2): ProjectionMatrix = {
      import gl._
      glPushMatrix()
      try {
        val array = new Array[Double](16)
        glLoadIdentity()
        glOrtho(wdt, -wdt, -hgt, hgt, nearPlane, farPlane)
        glGetDoublev(GL_MODELVIEW_MATRIX, array, 0)
        new ProjectionMatrix(array)
      } finally glPopMatrix()
    }

    def orthoView(xfrom: Double, yfrom: Double, zfrom: Double, xto: Double, yto: Double, zto: Double, xup: Double, yup: Double, zup: Double)(implicit gl: GL2): ModelviewMatrix = {
      import gl._
      glPushMatrix()
      try {
        val array = new Array[Double](16)
        glLoadIdentity()
        glu.gluLookAt(xfrom, yfrom, zfrom, xto, yto, zto, xup, yup, zup)
        glGetDoublev(GL_MODELVIEW_MATRIX, array, 0)
        new ModelviewMatrix(array)
      } finally glPopMatrix()
    }

    @inline def apply[T](m: Matrix)(block: =>T)(implicit gl: GL2): T = {
      import gl._
      val mt = m match {
        case pm: ProjectionMatrix => GL_PROJECTION
        case mm: ModelviewMatrix => GL_MODELVIEW
      }

      glMatrixMode(mt)
      glPushMatrix()
      try {
        glLoadMatrixd(m.array.asInstanceOf[Array[Double]], 0)
        block
      } finally {
        glMatrixMode(mt)
        glPopMatrix()
      }
    }
  }
}




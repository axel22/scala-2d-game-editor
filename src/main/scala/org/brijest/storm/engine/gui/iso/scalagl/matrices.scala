/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine
package gui.iso.scalagl



import collection._
import javax.media.opengl._
import GL._
import GL2._
import GL2ES1._
import GL2ES2._
import fixedfunc.GLMatrixFunc._
import javax.media.opengl.glu.GLU



abstract class Matrix(val array: Array[Double]) {
  protected def empty = new Array[Double](16)
  protected def newMatrix: this.type

  def mode: Int
  def matrixMode: Int

  def *[M <: Matrix](that: M)(implicit gl: GL2): this.type = {
    import gl._
    val m = newMatrix
    val arr = m.array

    glMatrixMode(m.mode)
    glPushMatrix()
    try {
      glLoadMatrixd(this.array, 0)
      glMultMatrixd(that.array, 0)
      glGetDoublev(m.matrixMode, arr, 0)
    } finally glPopMatrix()

    m
  }

  def to[M <: Matrix: Matrix.Ctor]: M = implicitly[Matrix.Ctor[M]].apply(array)

  override def toString = "%s(%s)".format(this.getClass.getSimpleName, array.mkString(", "))
}


object Matrix {
  type Ctor[M <: Matrix] = Array[Double] => M

  implicit val projectionCtor = a => new ProjectionMatrix(a)
  implicit val modelviewCtor = a => new ModelviewMatrix(a)
  implicit val textureCtor = a => new TextureMatrix(a)

  def apply[M <: Matrix: Ctor](a: Array[Float]) = implicitly[Ctor[M]].apply(a.map(_.toDouble))
}

final class ProjectionMatrix(a: Array[Double]) extends Matrix(a) {
  def mode = GL_PROJECTION
  def matrixMode = GL_PROJECTION_MATRIX
  protected final def newMatrix = new ProjectionMatrix(empty).asInstanceOf[this.type]
}

final class ModelviewMatrix(a: Array[Double]) extends Matrix(a) {
  def mode = GL_MODELVIEW
  def matrixMode = GL_MODELVIEW_MATRIX
  protected final def newMatrix = new ModelviewMatrix(empty).asInstanceOf[this.type]
}

final class TextureMatrix(a: Array[Double]) extends Matrix(a) {
  def mode = GL_TEXTURE
  def matrixMode = GL_TEXTURE_MATRIX
  protected final def newMatrix = new TextureMatrix(empty).asInstanceOf[this.type]
}


object matrices {
  val result = new Array[Int](1)

  def orthoProjection(wdt: Double, hgt: Double, nearPlane: Double, farPlane: Double)(implicit gl: GL2): ProjectionMatrix = {
    import gl._
    glGetIntegerv(GL_MATRIX_MODE, result, 0)
    val oldmode = result(0)
    glPushMatrix()
    glMatrixMode(GL_MODELVIEW_MATRIX)
    try {
      val array = new Array[Double](16)
      glLoadIdentity()
      glOrtho(wdt, -wdt, -hgt, hgt, nearPlane, farPlane)
      glGetDoublev(GL_MODELVIEW_MATRIX, array, 0)
      new ProjectionMatrix(array)
    } finally {
     glMatrixMode(oldmode)
     glPopMatrix()
   }
 }

  def orthoView(xfrom: Double, yfrom: Double, zfrom: Double, xto: Double, yto: Double, zto: Double, xup: Double, yup: Double, zup: Double)(implicit gl: GL2): ModelviewMatrix = {
    import gl._
    glGetIntegerv(GL_MATRIX_MODE, result, 0)
    val oldmode = result(0)
    glPushMatrix()
    glMatrixMode(GL_MODELVIEW_MATRIX)
    try {
      val array = new Array[Double](16)
      glLoadIdentity()
      glu.gluLookAt(xfrom, yfrom, zfrom, xto, yto, zto, xup, yup, zup)
      glGetDoublev(GL_MODELVIEW_MATRIX, array, 0)
      new ModelviewMatrix(array)
    } finally {
     glMatrixMode(oldmode)
     glPopMatrix()
   }
 }

}

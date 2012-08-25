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

  implicit val projectionCtor = a => new Projection(a)
  implicit val modelviewCtor = a => new Modelview(a)
  implicit val textureCtor = a => new Texture(a)

  def apply[M <: Matrix: Ctor](a: Array[Float]) = implicitly[Ctor[M]].apply(a.map(_.toDouble))

  final case class Projection(a: Array[Double]) extends Matrix(a) {
    def mode = GL_PROJECTION
    def matrixMode = GL_PROJECTION_MATRIX
    protected final def newMatrix = new Projection(empty).asInstanceOf[this.type]
  }

  final case class Modelview(a: Array[Double]) extends Matrix(a) {
    def mode = GL_MODELVIEW
    def matrixMode = GL_MODELVIEW_MATRIX
    protected final def newMatrix = new Modelview(empty).asInstanceOf[this.type]
  }

  final case class Texture(a: Array[Double]) extends Matrix(a) {
    def mode = GL_TEXTURE
    def matrixMode = GL_TEXTURE_MATRIX
    protected final def newMatrix = new Texture(empty).asInstanceOf[this.type]
  }

  trait Factory[M <: Matrix] {
    def apply(a: Array[Double]): M

    val identity: M = apply(Array(
      1.0, 0.0, 0.0, 0.0,
      0.0, 1.0, 0.0, 0.0,
      0.0, 0.0, 1.0, 0.0,
      0.0, 0.0, 0.0, 1.0
    ))
  }

  object Projection extends Factory[Projection]

  object Modelview extends Factory[Modelview]

  object Texture extends Factory[Texture]
}

object matrices {
  private val result = new Array[Int](1)

  def orthoProjection(matrixMode: Int, left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double)(implicit gl: GL2): Matrix.Projection = {
    import gl._
    glGetIntegerv(GL_MATRIX_MODE, result, 0)
    val oldmode = result(0)
    glMatrixMode(matrixMode)
    glPushMatrix()
    try {
      val array = new Array[Double](16)
      glLoadIdentity()
      glOrtho(left, right, bottom, top, nearPlane, farPlane)
      glGetDoublev(matrixMode, array, 0)
      new Matrix.Projection(array)
    } finally {
     glPopMatrix()
     glMatrixMode(oldmode)
   }
 }

  def orthoView(xfrom: Double, yfrom: Double, zfrom: Double, xto: Double, yto: Double, zto: Double, xup: Double, yup: Double, zup: Double)(implicit gl: GL2): Matrix.Modelview = {
    import gl._
    glGetIntegerv(GL_MATRIX_MODE, result, 0)
    val oldmode = result(0)
    glMatrixMode(GL_MODELVIEW_MATRIX)
    glPushMatrix()
    try {
      val array = new Array[Double](16)
      glLoadIdentity()
      glu.gluLookAt(xfrom, yfrom, zfrom, xto, yto, zto, xup, yup, zup)
      glGetDoublev(GL_MODELVIEW_MATRIX, array, 0)
      new Matrix.Modelview(array)
    } finally {
     glPopMatrix()
     glMatrixMode(oldmode)
   }
 }

}

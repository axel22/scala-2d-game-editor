package org.brijest.storm.engine
package gui



import javax.media.opengl._
import javax.media.opengl.awt.GLCanvas
import javax.media.opengl.glu.GLU
import GL._
import GL2._
import GL2ES1._
import GL2ES2._
import fixedfunc.GLLightingFunc._
import fixedfunc.GLMatrixFunc._
import org.scalagl._
import model._
import collection._



package object iso {
  
  def pngStream(name: String): java.io.InputStream = {
    getClass.getResourceAsStream("/iso/" + name + ".png")
  }
  
  def confStream(name: String): java.io.InputStream = {
    getClass.getResourceAsStream("/iso/" + name + ".conf")
  }
  
  object Sprites {
    def maxheight = 320
  }
  
  def ceilpow2(n: Int) = {
    var pow2 = 1
    while (n > pow2) {
      pow2 = pow2 << 1
    }
    pow2
  }

  /* model rendering */

  private[iso] def renderCube(x: Float, y: Float, xspan2: Float, yspan2: Float, bottom: Float, top: Float)(implicit gl: GL2) = geometry(GL_TRIANGLE_STRIP) {
    val xspan = xspan2 / 2
    val yspan = yspan2 / 2

    /* top */
    v3d(x - xspan, y - yspan, top)
    v3d(x - xspan, y + yspan, top)
    v3d(x + xspan, y - yspan, top)
    v3d(x + xspan, y + yspan, top)

    /* sides and bottom */
    if (top > 0) {
      v3d(x + xspan, y + yspan, bottom)
      v3d(x - xspan, y + yspan, top)
      v3d(x - xspan, y + yspan, bottom)
      v3d(x - xspan, y - yspan, top)
      v3d(x - xspan, y - yspan, bottom)
      v3d(x + xspan, y - yspan, top)
      v3d(x + xspan, y - yspan, bottom)
      v3d(x + xspan, y + yspan, bottom)
    }
  }

  private[iso] def renderTriPrism(x: Float, y: Float, xspan2: Float, yspan2: Float, bottom: Float, top: Float)(implicit gl: GL2) = geometry(GL_QUADS) {
    val xspan = xspan2 / 2
    val yspan = yspan2 / 2

    // TODO fix this to render actual triprism

    /* top */
    v3d(x - xspan, y - yspan, top)
    v3d(x - xspan, y + yspan, top)
    v3d(x + xspan, y + yspan, top)
    v3d(x + xspan, y - yspan, top)

    /* sides and bottom */
    if (top > 0) {
      v3d(x - xspan, y - yspan, top)
      v3d(x - xspan, y - yspan, bottom)
      v3d(x - xspan, y + yspan, bottom)
      v3d(x - xspan, y + yspan, top)

      v3d(x + xspan, y - yspan, top)
      v3d(x + xspan, y - yspan, bottom)
      v3d(x - xspan, y - yspan, bottom)
      v3d(x - xspan, y - yspan, top)

      v3d(x + xspan, y + yspan, top)
      v3d(x + xspan, y + yspan, bottom)
      v3d(x + xspan, y - yspan, bottom)
      v3d(x + xspan, y - yspan, top)

      v3d(x - xspan, y + yspan, top)
      v3d(x - xspan, y + yspan, bottom)
      v3d(x + xspan, y + yspan, bottom)
      v3d(x + xspan, y + yspan, top)
    }
  }

  private[iso] def renderRectangle(xtl: Float, ytl: Float, xbr: Float, ybr: Float, height: Float)(implicit gl: GL2) = geometry(GL_QUADS) {
    v3d(xtl, ybr, height)
    v3d(xbr, ybr, height)
    v3d(xbr, ytl, height)
    v3d(xtl, ytl, height)
  }

  def renderShape(x: Int, y: Int, s: Shape, hgt: Float)(implicit gl: GL2): Unit = s match {
    case Shape.Cube(xd, yd, zd, xoff, yoff, zoff) =>
      val bottom = hgt + zoff
      renderCube(x + xoff, y + yoff, xd, yd, bottom, bottom + zd)
    case Shape.TriPrism(xd, yd, zd, xoff, yoff, zoff) =>
      val bottom = hgt + zoff
      renderTriPrism(x + xoff, y + yoff, xd, yd, bottom, bottom + zd)
    case Shape.Composite(subs) =>
      for (sub <- subs) renderShape(x, y, sub, hgt)
    case Shape.None =>
      // do nothing
  }  
  
}



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



trait Handle[H <: Handle[H]] {

  def acquire()(implicit gl: GL2): Unit

  def release()(implicit gl: GL2): Unit

}


final class ShaderProgram private[scalagl] () extends Handle[ShaderProgram] {

  private[scalagl] var pindex = -1
  private var spname = ""
  private val result = new Array[Int](1)
  private val errorbuff = new Array[Byte](1000)

  trait Shader {
    val shaders = mutable.Map[String, Int]()

    private def processErrors(shname: String, shader: Int)(implicit gl: GL2) {
      import gl._
      glGetShaderiv(shader, GL_COMPILE_STATUS, result, 0)
      if (result(0) == GL_FALSE) {
        glGetShaderInfoLog(shader, errorbuff.length, result, 0, errorbuff, 0)
        val comperrors = errorbuff.map(_.toChar).mkString
        throw new ShaderProgram.Exception("error compiling %s shader in %s\n%s".format(shname, spname, comperrors))
      }
    }

    def mode: Int

    def attach(sname: String, srcs: String*)(implicit gl: GL2) {
      import gl._
      val s = glCreateShader(mode)
      glShaderSource(s, srcs.length, srcs.toArray, null)
      glCompileShader(s)
      processErrors(sname, s)
      glAttachShader(pindex, s)
      glLinkProgram(pindex)
      glValidateProgram(pindex)
    }

    def release()(implicit gl: GL2) {
      for ((nm, s) <- shaders) gl.glDeleteShader(s)
      shaders.clear()
    }
  }

  object vertex extends Shader {
    def mode = GL2ES2.GL_VERTEX_SHADER
  }

  object fragment extends Shader {
    def mode = GL2ES2.GL_FRAGMENT_SHADER
  }

  object uniform {
    trait Sink {
      def <<(v: Int): Unit
    }

    @inline def apply(varname: String)(implicit gl: GL2) = new Sink {
      def location = {
        val loc = gl.glGetUniformLocation(index, varname)
        if (loc == -1) new ShaderProgram.Exception("could not send uniform: " + varname)
        loc
      }
      def <<(v: Int) = gl.glUniform1i(location, v)
      def <<(x: Float, y: Float, z: Float): Unit = gl.glUniform3f(location, x, y, z)
      def <<(t: (Float, Float, Float)): Unit = <<(t._1, t._2, t._3)
    }
  }

  def name = spname

  def name_=(n: String) {
    if (pindex != -1) {
      spname = n
    }
  }

  def index = pindex

  def acquire()(implicit gl: GL2) {
    release()
    pindex = gl.glCreateProgram()
  }

  def release()(implicit gl: GL2) {
    if (pindex != -1) {
      gl.glDeleteProgram(pindex)
      vertex.release()
      fragment.release()
    }
  }

}


object ShaderProgram {
  case class Exception(msg: String) extends java.lang.Exception(msg)

  def apply() = new ShaderProgram()
}


abstract class Texture extends Handle[Texture] {

  def release()(implicit gl: GL2) {
    // TODO
  }

}


abstract class FrameBuffer extends Handle[FrameBuffer] {

  def release()(implicit gl: GL2) {
    // TODO
  }

}
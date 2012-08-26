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


final class ShaderProgram private[scalagl] (nm: String) extends Handle[ShaderProgram] {

  private[scalagl] var pindex = -1
  private val result = new Array[Int](1)
  private val errorbuff = new Array[Byte](1000)
  val name = nm

  trait Shader {
    val shaders = mutable.Map[String, Int]()

    private def processErrors(shname: String, shader: Int)(implicit gl: GL2) {
      import gl._
      glGetShaderiv(shader, GL_COMPILE_STATUS, result, 0)
      if (result(0) == GL_FALSE) {
        glGetShaderInfoLog(shader, errorbuff.length, result, 0, errorbuff, 0)
        val comperrors = errorbuff.map(_.toChar).mkString
        throw new ShaderProgram.Exception("error compiling %s shader in program %s\n%s".format(shname, name, comperrors))
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

  object uniform extends Dynamic {
    trait Var {
      def :=(v: Int)
      def :=(x: Float, y: Float, z: Float)
      def :=(t: (Float, Float, Float))
    }

    @inline def applyDynamic(varname: String)()(implicit gl: GL2) = new Var {
      def location = {
        val loc = gl.glGetUniformLocation(index, varname)
        if (loc == -1) new ShaderProgram.Exception("could not send uniform: " + varname)
        loc
      }
      def :=(v: Int) = gl.glUniform1i(location, v)
      def :=(x: Float, y: Float, z: Float): Unit = gl.glUniform3f(location, x, y, z)
      def :=(t: (Float, Float, Float)): Unit = :=(t._1, t._2, t._3)
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
      pindex = -1
      vertex.release()
      fragment.release()
    }
  }

}


object ShaderProgram {
  case class Exception(msg: String) extends java.lang.Exception(msg)

  def apply(name: String) = new ShaderProgram(name)
}


final class Texture(val target: Int) extends Handle[Texture] {

  private[scalagl] var tindex = -1
  private val result = new Array[Int](1)

  def index = tindex

  def binding = target match {
    case GL_TEXTURE_2D => GL_TEXTURE_BINDING_2D
    case _ => throw new UnsupportedOperationException
  }

  def acquire()(implicit gl: GL2) {
    import gl._
    release()
    glGenTextures(1, result, 0)
    tindex = result(0)
  }

  def update(name: Int, v: Float)(implicit gl: GL2) {
    gl.glBindTexture(GL_TEXTURE_2D, tindex)
    gl.glTexParameterf(target, name, v)
  }

  def update(name: Int, v: Int)(implicit gl: GL2) {
    gl.glBindTexture(GL_TEXTURE_2D, tindex)
    gl.glTexParameteri(target, name, v)
  }

  def apply(name: Int)(implicit gl: GL2): Int = {
    gl.glGetTexParameteriv(target, name, result, 0)
    result(0)
  }

  def minFilter(implicit gl: GL2) = this(GL_TEXTURE_MIN_FILTER)

  def minFilter_=(v: Int)(implicit gl: GL2) = this(GL_TEXTURE_MIN_FILTER) = v

  def magFilter(implicit gl: GL2) = this(GL_TEXTURE_MAG_FILTER)

  def magFilter_=(v: Int)(implicit gl: GL2) = this(GL_TEXTURE_MAG_FILTER) = v

  def wrapS(implicit gl: GL2) = this(GL_TEXTURE_WRAP_S)

  def wrapS_=(v: Int)(implicit gl: GL2) = this(GL_TEXTURE_WRAP_S) = v

  def wrapT(implicit gl: GL2) = this(GL_TEXTURE_WRAP_T)

  def wrapT_=(v: Int)(implicit gl: GL2) = this(GL_TEXTURE_WRAP_T) = v

  def compareMode(implicit gl: GL2) = this(GL_TEXTURE_COMPARE_MODE)

  def compareMode_=(v: Int)(implicit gl: GL2) = this(GL_TEXTURE_COMPARE_MODE) = v

  def compareFunc(implicit gl: GL2) = this(GL_TEXTURE_COMPARE_FUNC)

  def compareFunc_=(v: Int)(implicit gl: GL2) = this(GL_TEXTURE_COMPARE_FUNC) = v

  def depthTextureMode(implicit gl: GL2) = this(GL_DEPTH_TEXTURE_MODE)

  def depthTextureMode_=(v: Int)(implicit gl: GL2) = this(GL_DEPTH_TEXTURE_MODE) = v

  def allocateImage(level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int)(implicit gl: GL2) {
    target match {
      case GL_TEXTURE_2D => gl.glTexImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, null)
      case _ => throw new UnsupportedOperationException
    }
  }

  def release()(implicit gl: GL2) {
    if (tindex != -1) {
      result(0) = tindex
      gl.glDeleteTextures(1, result, 0)
      tindex = -1
    }
  }

}


final class FrameBuffer extends Handle[FrameBuffer] {

  private[scalagl] var fbindex = -1
  private val result = new Array[Int](1)

  def index = fbindex

  private[scalagl] object binding {
    def attachTexture2D(target: Int, attachment: Int, t: Texture, level: Int)(implicit gl: GL2): Setup[Null] = new Setup[Null] {
      def foreach[U](f: Null => U) {
        gl.glFramebufferTexture2D(target, attachment, t.target, t.index, level)
        try f(null)
        finally {
        }
      }
    }
  }

  def acquire()(implicit gl: GL2) {
    release()
    gl.glGenFramebuffers(1, result, 0)
    fbindex = result(0)
  }

  def release()(implicit gl: GL2) {
    if (fbindex != -1) {
      result(0) = fbindex
      gl.glDeleteFramebuffers (1, result, 0)
      fbindex = -1
    }
  }

}








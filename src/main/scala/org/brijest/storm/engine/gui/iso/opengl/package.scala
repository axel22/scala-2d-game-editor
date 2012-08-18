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



package object opengl {

  @inline def geometry[T](geomtype: Int)(block: =>T)(implicit gl: GL2) {
    import gl._
    gl.glBegin(geomtype)
    block
    glEnd()
  }

  @inline def v(x: Float, y: Float, z: Float)(implicit gl: GL2) {
    gl.glVertex3f(x, y, z)
  }

  @inline def v(x: Double, y: Double, z: Double)(implicit gl: GL2) {
    gl.glVertex3d(x, y, z)
  }
  
}




/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest



import com.weiglewilczek._
import collection._



package object storm {
  
  def illegalarg(msg: String) = throw new IllegalArgumentException(msg)
  
  def illegalarg(obj: Any) = throw new IllegalArgumentException(obj.toString)
  
  def illegalstate(msg: String) = throw new IllegalStateException(msg)
  
  def unsupported(obj: Any*) = throw new UnsupportedOperationException(obj.mkString(", "))
  
  def exit(msg: String): Nothing = {
    Console.err.println(msg)
    sys.exit(1)
    sys.error("unreachable")
  }
  
  /* logging */
  
  val Logger = slf4s.Logger
  
  type Logger = slf4s.Logger
  
  type Logging = slf4s.Logging
  
  /* pimps */
  
  implicit def pair2numeric[T: Numeric](p: (T, T)) = new {
    def +(q: (T, T)) = (implicitly[Numeric[T]].plus(p._1, q._1), implicitly[Numeric[T]].plus(p._2, q._2))
    def -(q: (T, T)) = (implicitly[Numeric[T]].minus(p._1, q._1), implicitly[Numeric[T]].minus(p._2, q._2))
  }
  
  /* utils */

  def timed(thunk: =>Unit) = {
    val t1 = System.currentTimeMillis
    thunk
    val time = System.currentTimeMillis - t1
    time
  }
  
  /* constants */
  
  object app {
    val name = "Storm Enroute"
    val editorname = "Storm Enroute Area Editor"
    val command = "storm-enroute"
    val editorcommand = "editor"
    
    trait PropMap extends Dynamic {
      private val propmap = mutable.Map[String, Any]()
      
      def applyDynamic[T](name: String)(v: T*): T = {
        if (v.length > 0) propmap(name) = v(0)
        propmap(name).asInstanceOf[T]
      }
    }
    
    object render extends PropMap {
      this.outline(false)
      this.seethrough(false)
      this.indices(true)
      this.background(true)
      this.shadows(sys.os != "Linux")
    }
    
    object dir {
      val separator = java.io.File.separator
      val worlds = "worlds"
      val libs = "lib"
      
      def path(dirs: String*) = {
        app.sys.props("user.dir") + dirs.mkString(app.dir.separator, app.dir.separator, app.dir.separator)
      }
    }
    
    object sys {
      
      object props {
        def apply(name: String) = scala.sys.props(name)
        
        def update(name: String, value: String) {
          scala.sys.props(name) = value
          
          val fieldSysPath = classOf[ClassLoader].getDeclaredField("sys_paths");
          fieldSysPath.setAccessible( true );
          fieldSysPath.set( null, null );
        }
      }
      
      val os = props("os.name")
    }
  }
  
}





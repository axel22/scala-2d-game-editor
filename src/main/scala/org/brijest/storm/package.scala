/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest







package object storm {
  
  def illegalarg(msg: String) = throw new IllegalArgumentException(msg)
  
  def illegalarg(obj: Any) = throw new IllegalArgumentException(obj.toString)
  
  def illegalstate(msg: String) = throw new IllegalStateException(msg)
  
  def unsupported(obj: Any*) = throw new UnsupportedOperationException(obj.mkString(", "))
  
  def exit(msg: String): Nothing = {
    Console.err.println(msg)
    System.exit(1)
    sys.error("unreachable")
  }
  
  /* pimps */
  
  implicit def pair2numeric[T: Numeric](p: (T, T)) = new {
    def +(q: (T, T)) = (implicitly[Numeric[T]].plus(p._1, q._1), implicitly[Numeric[T]].plus(p._2, q._2))
    def -(q: (T, T)) = (implicitly[Numeric[T]].minus(p._1, q._1), implicitly[Numeric[T]].minus(p._2, q._2))
  }
  
  /* constants */
  
  object app {
    val name = "Storm Enroute"
    val command = "storm-enroute"
  }
  
}

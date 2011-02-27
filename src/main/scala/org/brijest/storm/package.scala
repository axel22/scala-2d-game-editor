package org.brijest







package object storm {
  
  def illegalarg(msg: String) = throw new IllegalArgumentException(msg)
  
  def illegalarg(obj: Any) = throw new IllegalArgumentException(obj.toString)
  
  def illegalstate(msg: String) = throw new IllegalStateException(msg)
  
  def unsupported(obj: Any*) = throw new UnsupportedOperationException(obj.mkString(", "))
  
  def exit(msg: String): Nothing = {
    Console.err.println(msg)
    System.exit(1)
    error("unreachable")
  }
  
  /* constants */
  
  object app {
    val name = "Storm Enroute"
    val command = "storm-enroute"
  }
  
}

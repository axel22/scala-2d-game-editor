package org.brijest







package object storm {
  
  def illegalarg(msg: String) = throw new IllegalArgumentException(msg)
  
  def illegalarg(obj: Any) = throw new IllegalArgumentException(obj.toString)
  
}

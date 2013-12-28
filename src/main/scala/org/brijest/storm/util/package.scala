package org.brijest.storm



import collection._



package object util {
  
  def consume[T](body: =>T)(loop: T => Boolean): Seq[T] = {
    val buffer = mutable.Buffer[T]()
    while (true) {
      println(buffer)
      val v = body
      if (!loop(v)) return buffer
      else buffer += v
    }
    sys.error("unreachable")
  }
  
}

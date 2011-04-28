/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm
package engine.model
package rules






trait Attributes extends BasicStats {
  def apply(s: Symbol): Int
  def delay = apply('delay)
  def heightStride = apply('heightStride)
}


object Attributes {
  def apply(xs: (Symbol, Int)*) = new Attributes {
    val attrmap = xs.toMap
    def apply(s: Symbol) = attrmap(s)
  }
}

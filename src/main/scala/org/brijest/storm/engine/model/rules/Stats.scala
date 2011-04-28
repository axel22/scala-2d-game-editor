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






trait Stats extends BasicStats {
  def apply(s: Symbol): Stat
  def mainstats: Seq[Symbol]
  
  def delay = apply('delay).asNat.v
  def heightStride = apply('heightStride).asNat.v
}


object Stats {
  def apply(xs: (Symbol, Stat)*)(main: Symbol*) = new Stats {
    val statmap = xs.toMap
    def apply(s: Symbol) = statmap(s)
    def mainstats = main
  }
}


trait Stat {
  def asNat = this.asInstanceOf[Nat]
  def asFract = this.asInstanceOf[Fract]
  def niceString: String
}


case class Nat(v: Int) extends Stat {
  def niceString = v.toString
}


case class Fract(v: Int, max: Int) extends Stat {
  def niceString = "%d/%d".format(v, max)
}

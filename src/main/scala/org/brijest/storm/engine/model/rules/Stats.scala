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
  def all: Map[Symbol, Stat]
  def main: Map[Symbol, Stat]
  def attributes: Map[Symbol, Stat]
  
  def delay = apply('delay).asNat.v
  def heightStride = apply('heightStride).asNat.v
  def encumbrance = apply('encumbrance).asNat.v
}


object Stats {
  def apply(m: Map[Symbol, Stat]): Stats = apply(m.toSeq: _*)
  def apply(xs: (Symbol, Stat)*) = new Stats {
    val statmap = xs.toMap
    def apply(s: Symbol) = statmap(s)
    def all = statmap
    def main = statmap.filter(_._2.isMain)
    def attributes = statmap.filter(_._2.isAttribute)
  }
}


trait Stat {
  def asNat = this.asInstanceOf[Nat]
  def asFract = this.asInstanceOf[Fract]
  def niceString: String
  def isMain = false
  def isAttribute = false
}


trait Main extends Stat {
  override def isMain = true
}


trait Attribute extends Stat {
  override def isAttribute = true
}


case class Nat(v: Int) extends Stat {
  def niceString = v.toString
}


case class Fract(v: Int, max: Int) extends Stat {
  def niceString = "%d/%d".format(v, max)
}



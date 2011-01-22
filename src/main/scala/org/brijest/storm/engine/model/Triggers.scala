package org.brijest.storm.engine
package model






sealed trait Trigger


final case class AfterTime(turns: Int) extends Trigger



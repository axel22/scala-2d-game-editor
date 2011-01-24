package org.brijest.storm.engine
package model






sealed trait Trigger


object NoTrigger extends Trigger


final case class AfterTime(turns: Int) extends Trigger



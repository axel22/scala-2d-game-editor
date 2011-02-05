package org.brijest.storm.engine
package model



import org.triggerspace.ImmutableValue



sealed trait Trigger extends ImmutableValue


object NoTrigger extends Trigger


final case class AfterTime(turns: Int) extends Trigger



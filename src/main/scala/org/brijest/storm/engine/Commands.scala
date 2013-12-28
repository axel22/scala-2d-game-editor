package org.brijest.storm.engine



import model._



trait Command


case class OrderCommand(plid: PlayerId, order: Order) extends Command


case class ScriptCommand(script: String) extends Command


case object EmptyCommand extends Command

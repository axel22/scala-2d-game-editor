package org.brijest.storm.engine



import model.Player



trait Engine extends Engine.State {
  def start(): Unit
  def push(comm: Command): Unit
  def script(m: String): Unit
  def listen(ui: UI)
  def player: Player
  def awaitTermination(): Unit
}


object Engine {
  trait State {
    def isPaused: Boolean
  }
}


object IdleEngine extends Engine {
  def start() {}
  def push(comm: Command) {}
  def script(m: String) {}
  def listen(ui: UI) {}
  def player = Player.default(model.invalidPlayerId)
  def awaitTermination() {}
  def isPaused = false
}

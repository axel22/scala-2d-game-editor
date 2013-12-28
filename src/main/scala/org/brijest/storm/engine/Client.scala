package org.brijest.storm.engine






class Client(engine: Engine, ui: UI) {
  def awaitTermination() = engine.awaitTermination()
}


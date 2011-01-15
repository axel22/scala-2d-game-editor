

import sbt._





class StormEnroute(info: ProjectInfo) extends DefaultProject(info) {
  
  lazy val bufferz = project("subs" / "bufferz")
  lazy val triggerspace = project("subs" / "triggerspace")
  
}

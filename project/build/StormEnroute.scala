

import sbt._





class StormEnroute(info: ProjectInfo) extends ParentProject(info) {
  
  lazy val bufferz = project("subs" / "bufferz")
  lazy val triggerspace = project("subs" / "triggerspace")
  
}

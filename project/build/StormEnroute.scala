

import sbt._





class StormEnroute(info: ProjectInfo) extends DefaultProject(info) {
  
  /* subprojects */
  
  lazy val bufferz = project("subs" / "bufferz")
  lazy val triggerspace = project("subs" / "triggerspace")
  
  /* dependencies */
  
  val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.3"
  
}

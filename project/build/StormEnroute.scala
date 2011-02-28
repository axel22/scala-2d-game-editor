

import sbt._





class StormEnroute(info: ProjectInfo) extends DefaultProject(info) {
  
  /* subprojects */
  
  lazy val bufferz = project("subs" / "bufferz")
  lazy val triggerspace = project("subs" / "triggerspace")
  
  /* dependencies */
  
  val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.3"
  //val scopt = "com.github.scopt" % "scopt" % "1.0-SNAPSHOT" - not found?
  val h2db = "com.h2database" % "h2" % "1.2.147"
  
}



import sbt._





class StormEnroute(info: ProjectInfo) extends DefaultProject(info) {
  
  /* subprojects */
  
  lazy val bufferz = project("subs" / "bufferz")
  lazy val triggerspace = project("subs" / "triggerspace")
  
  /* dependencies */
  
  //val scopt = "com.github.scopt" % "scopt" % "1.0-SNAPSHOT" - not found?
  val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.3"
  val slf4j_jdk = "org.slf4j" % "slf4j-jdk14" % "1.6.1"
  val h2db = "com.h2database" % "h2" % "1.2.147"
  
}

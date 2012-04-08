/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */




import sbt._
import Keys._
import Process._
import java.io.File



object StormEnrouteBuild extends Build {
  
  /* tasks */
  
  val deployTask = TaskKey[Unit](
    "deploy",
    "Deploys the project in the `deploy` subdirectory."
  ) <<= (artifactPath in (Compile, packageBin), dependencyClasspath in Compile) map {
    (artifact, classpath) =>
    val deploydir = new File("deploy")
    val libzdir = new File("deploy%slib".format(File.separator))
    val editorscript = new File("deploy%seditor".format(File.separator))
    
    // clean old subdirectory
    deploydir.delete()
    
    // create subdirectory structure
    deploydir.mkdir()
    libzdir.mkdir()
    
    // copy deps and artifacts
    val fullcp = classpath.map(_.data) :+ artifact
    def lastName(file: File) = if (file.isFile) file.getName else file.getParentFile.getParentFile.getParentFile.getName
    for (file <- fullcp) {
      println("Copying: " + file + "; lastName: " + lastName(file))
      if (file.isFile) file #> (libzdir / lastName(file)).asFile !;
      else IO.copyDirectory(file, (libzdir / lastName(file)))
    }
    
    // create run scripts
    IO.write(editorscript, Scripts.editor(fullcp.map(lastName(_))))
  } dependsOn (packageBin in Compile)
  
  
  /* projects and dependencies */
  
  lazy val mempool = RootProject(uri("git://github.com/axel22/scalapool.git#HEAD"))
  
  lazy val bufferz = RootProject(uri("git://git.assembla.com/bufferz.git#HEAD"))
  
  lazy val storm = Project(
    "storm-enroute",
    file("."),
    settings = Defaults.defaultSettings ++ Seq(deployTask)
  ) dependsOn (
    mempool,
    bufferz
  )
  
}


object Scripts {
  
  val flags = "-Dsun.java2d.opengl=True"
  
  def editor(jars: Seq[String]) =
    """#!/bin/sh
BASEDIR=`dirname $0`
JARS=%s
java -server -classpath $JARS %s org.brijest.storm.Editor "$@"
""".format(
    jars.map("$BASEDIR/lib/" + _).mkString(":"),
    flags
  )
  
}




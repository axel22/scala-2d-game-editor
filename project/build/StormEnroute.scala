/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                          Storm Enroute (c) 2011        **
\*                                          www.storm-enroute.com         */


import sbt._
import Process._
import java.io._



class StormEnroute(info: ProjectInfo) extends DefaultProject(info) {
  
  /* subprojects */
  
  lazy val bufferz = project("subs" / "bufferz")
  
  /* config */
  
  val scalaVersion = "2.9.0"
  
  /* dependencies */
  
  //val scopt = "com.github.scopt" % "scopt" % "1.0-SNAPSHOT" - not found?
  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"
  val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.6"
  val slf4j_jdk = "org.slf4j" % "slf4j-jdk14" % "1.6.1"
  val h2db = "com.h2database" % "h2" % "1.2.147"
  val scalaSwing = "org.scala-lang" % "scala-swing" % scalaVersion
  val apacheCommons = "commons-lang" % "commons-lang" % "2.2"
  val apacheCommonsIo = "commons-io" % "commons-io" % "1.3.2"
  
  /* constants */
  
  def scalavers = scalaVersion
  
  def fullArtifactName(artname: String) = artname + "-" + version + "." + defaultMainArtifact.extension
  
  def artifactname = fullArtifactName(defaultMainArtifact.name)
  
  def artifactpath = "target" / ("scala_" + scalavers) / artifactname
  
  def scalalibpath = "project" / "boot" / ("scala-" + scalavers) / "lib" / "scala-library.jar"
  
  override def mainClass = Some("org.brijest.storm.StormEnroute")
  
  def editorClass = Some("org.brijest.storm.Editor")
  
  def classpath =
    unmanagedClasspath.get ++
    managedClasspath(Configurations.Runtime).get ++ 
    subartifacts
  
  def subartifacts = List(
    bufferz.outputPath / fullArtifactName("bufferz_" + scalavers),
  )
  
  /* helpers */
  
  def loginfo(msg: String) = log.log(Level.Info, msg)
  
  def runsync(com: String) {
    loginfo("Running: " + com)
    com !;
  }
  
  var asyncs = List[(Process, String)]()
  
  def runasync(command: String) {
    loginfo("running: " + command)
    val p = command run;
    asyncs ::= (p, command)
  }
  
  def copyDependencies(dir: Path) {
    artifactpath.asFile #> (dir / artifactname).asFile !;
    scalalibpath.asFile #> (dir / fileName(scalalibpath)).asFile !;
    for (p <- classpath) {
      p.asFile #> (dir / fileName(p)).asFile !;
    }
  }
  
  def fileName(f: Path) = f.asFile.getName
  
  def withFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
  
  def createRunScript(name: String, dir: Path, libdir: Path, comment: String, maincls: String, bat: Boolean) {
    def declare(nm: String, v: String) = if (bat) "set %s=%s".format(nm, v) else "%s=%s".format(nm, v)
    def variable(nm: String) = if (bat) "%" + nm + "%" else "$" + nm
    def delimiter = if (bat) ";" else ":"
    val alljars = List(Deploy.dir / artifactname) ++ classpath ++ List(scalalibpath)
    val jarstring = "%s".format(alljars.map(fileName(_)).map(libdir.asFile.getName / _).mkString(delimiter))
    val jardecl = declare("JARS", jarstring)
    val flags = "-Dsun.java2d.opengl=True"
    val startcommand = "java -cp %s %s %s %s".format(
      variable("JARS"),
      flags,
      maincls,
      "$@"
    )
    
    val filename = name + (if (bat) ".bat" else "")
    val startscript = (dir / filename).asFile
    withFile(startscript) {
      p =>
      p.println("# %s".format(comment))
      p.println("%s".format(jardecl))
      p.println("%s".format(startcommand))
    }
    "chmod a+x %s".format(startscript) !;
    
    if (!bat) createRunScript(name, dir, libdir, comment, maincls, true)
  }
  
  def createBaseDirRunScript(name: String, runscr: String, dir: Path) {
    val f = new File(name)
    withFile(f) {
      p =>
      p.println("cd %s".format(dir))
      p.println("./%s $@".format(runscr))
    }
    "chmod a+x %s".format(name) !;
  }
  
  object Deploy {
    val stormcmd = "storm-enroute"
    val editorcmd = "editor"
    val dir = "deploy"
    val savedir = dir / "saves"
    val libzdir = dir / "libz"
    val areadir = dir / "areas"
  }
  
  /* tasks */
  
  lazy val deploy = task {
    runsync("rm -rf %s".format(Deploy.savedir))
    runsync("mkdir %s".format(Deploy.dir))
    runsync("mkdir %s".format(Deploy.libzdir))
    runsync("mkdir %s".format(Deploy.areadir))
    copyDependencies(Deploy.libzdir)
    createRunScript(Deploy.stormcmd, Deploy.dir, Deploy.libzdir, "Storm Enroute", mainClass.get, true)
    createRunScript(Deploy.editorcmd, Deploy.dir, Deploy.libzdir, "Editor", editorClass.get, true)
    createBaseDirRunScript("deployrun", Deploy.stormcmd, Deploy.dir)
    None
  } dependsOn (`package`)
  
  lazy val deployRun = task { args =>
    task {
      runasync("./deployrun %s".format(args.mkString(" ")))
      None
    } dependsOn (deploy)
  }
  
  lazy val listAsyncs = task {
    loginfo("processes: ")
    for ((p, c) <- asyncs) {
      loginfo(p.toString + ": " + c)
    }
    None
  }
  
  lazy val destroyAsyncs = task {
    for ((p, _) <- asyncs) {
      p.destroy()
    }
    asyncs = Nil
    None
  }  
  
}

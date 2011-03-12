

import sbt._
import Process._
import java.io._



class StormEnroute(info: ProjectInfo) extends DefaultProject(info) {
  
  /* subprojects */
  
  lazy val bufferz = project("subs" / "bufferz")
  lazy val triggerspace = project("subs" / "triggerspace")
  
  /* dependencies */
  
  //val scopt = "com.github.scopt" % "scopt" % "1.0-SNAPSHOT" - not found?
  val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.3"
  val slf4j_jdk = "org.slf4j" % "slf4j-jdk14" % "1.6.1"
  val h2db = "com.h2database" % "h2" % "1.2.147"
  
  /* constants */
  
  def scalavers = "2.8.1"
  
  def fullArtifactName(artname: String) = artname + "-" + version + "." + defaultMainArtifact.extension
  
  def artifactname = fullArtifactName(defaultMainArtifact.name)
  
  def artifactpath = "target" / ("scala_" + scalavers) / artifactname
  
  def scalalibpath = "project" / "boot" / ("scala-" + scalavers) / "lib" / "scala-library.jar"
  
  override def mainClass = Some("org.brijest.storm.StormEnroute")
  
  def classpath =
    unmanagedClasspath.get ++
    managedClasspath(Configurations.Runtime).get ++ 
    subartifacts ++
    subdependencies.get
  
  def subartifacts = List(
    bufferz.outputPath / fullArtifactName("bufferz_" + scalavers),
    triggerspace.outputPath / fullArtifactName("triggerspace_" + scalavers)
  )
  
  def subdependencies =
    "subs" / "bufferz" / "lib_managed" / ("scala_" + scalavers) ** "*.jar"
  
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
  
  def createRunScript(name: String, dir: Path, libdir: Path) {
    val alljars = List(Deploy.dir / artifactname) ++ classpath ++ List(scalalibpath)
    val startcommand = "java -cp %s %s %s".format(
      alljars.map(fileName(_)).map(libdir.asFile.getName / _).mkString(":"),
      mainClass.get,
      "$@"
    )
    val startscript = (dir / name).asFile
    "echo %s".format(startcommand) #> startscript !;
    "chmod a+x %s".format(startscript) !;
  }
  
  def createBaseDirRunScript(name: String, runscr: String, dir: Path) {
    val f = new File(name)
    "echo cd %s".format(dir) #> f !;
    "echo ./%s $@".format(runscr) #>> f !;
    "chmod a+x %s".format(name) !;
  }
  
  object Deploy {
    val stormcmd = "storm-enroute"
    val dir = "deploy"
    val libzdir = dir / "libz"
  }
  
  /* tasks */
  
  lazy val deployRun = task { args =>
    task {
      runsync("mkdir %s".format(Deploy.dir))
      runsync("mkdir %s".format(Deploy.libzdir))
      copyDependencies(Deploy.libzdir)
      createRunScript(Deploy.stormcmd, Deploy.dir, Deploy.libzdir)
      createBaseDirRunScript("deployrun", Deploy.stormcmd, Deploy.dir)
      runasync("./deployrun %s".format(args.mkString(" ")))
      None
    } dependsOn (`package`)
  }
  
  lazy val listAsyncs = task {
    loginfo("processes: ")
    for ((p, c) <- asyncs) {
      loginfo(p.toString + ": " + c)
    }
    None
  }
  
  lazy val destroyAsyncs = task {
    for ((p, c) <- asyncs) {
      p.destroy()
    }
    asyncs = Nil
    None
  }  
  
}

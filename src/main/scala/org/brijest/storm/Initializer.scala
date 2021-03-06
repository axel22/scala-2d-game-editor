package org.brijest.storm



import java.io.File
import org.apache.commons.io.FileUtils
import collection._
import engine._
import engine.model.{World, Player}



object Initializer extends Logging {
  
  import Config._
  import impl._
  
  private def unpackNativeLibs() {
    val os = sys.props("os.name")
    val natstr = os match {
      case "Linux" => "linux"
      case "Mac OS X" => "macosx"
      case os if os.startsWith("Windows") => "windows"
      case os => sys.error("Unknown OS: " + os)
    }
    val natext = os match {
      case "Linux" => "so"
      case "Mac OS X" => "jnilib"
      case os if os.startsWith("Windows") => "dll"
      case os => sys.error("Unknown OS: " + os)
    }
    def rename(jarfile: File) = {
      val name = jarfile.getName
      if (name.contains("jogl-all-native") || name.contains("gluegen-rt-native")) {
        val newname = name.replaceAll("(.*)(natives-.*)-(\\d.\\d-rc\\d)\\.jar", "$1$3-$2.jar")
        val nfile = new File("lib/" + newname)
        if (jarfile.renameTo(nfile)) nfile else jarfile
      } else jarfile
    }
    
    val natives = mutable.ArrayBuffer[String]()
    val candidates = for {
      jarfile <- new File("lib/").listFiles
      if jarfile.getName.endsWith(".jar") && jarfile.getName.contains(natstr)
    } {
      val njarfile = rename(jarfile)
      val archive = new java.util.zip.ZipFile(njarfile)
      val entries = archive.entries
      while (entries.hasMoreElements) {
        val entry = entries.nextElement()
        if (entry.getName.contains(natext)) {
          FileUtils.copyInputStreamToFile(archive.getInputStream(entry), new File("lib/" + entry.getName))
          natives += entry.getName
        }
      }
    }
    logger.info("unpacked native libs: " + natives.mkString(", "))
  }
  
  def default() {
    // set java library path
    app.sys.props("java.library.path") = "lib/" + java.io.File.pathSeparator + app.sys.props("java.library.path")
    logger.info("java.library.path = " + app.sys.props("java.library.path"))
    
    // unpack gluegen-rt and jogl native libs - not necessary any more
    unpackNativeLibs()
  }
  
  def apply(config: Config): Client = {
    // setup debug info
    config.logging match {
      case Some(logging.screen) =>
        import java.util.logging._
        LogManager.getLogManager.readConfiguration(javaLoggingScreen)
      case _ =>
    }
    
    // setup engine
    val ng = config.engine match {
      case engine.local => new local.LocalEngine(config, Player.default(model.defaultPlayerId), createWorld(config))
      case e => exit("Engine '%s' not recognized.".format(e))
    }
    
    // setup ui
    val ui = createUI(config)
    
    // start engine
    ui.engine = Some(ng)
    ng.listen(ui)
    ng.start()
    
    new Client(ng, ui)
  }
  
  private def createUI(config: Config): UI = config.ui match {
    // TODO
    case e => exit("User interface '%s' not recognized".format(e))
  }
  
  private def createWorld(config: Config): World = config.world match {
    case None => new World.TestWorld
    case Some(_) => exit("Arbitrary worlds not yet supported.")
  }
  
  private def javaLoggingScreen = {
    import java.io._
    val conf =
"""
handlers=java.util.logging.ConsoleHandler
.level=INFO
java.util.logging.ConsoleHandler.level=INFO
"""
    new ByteArrayInputStream(conf.getBytes("UTF-8"))
  }
  
}

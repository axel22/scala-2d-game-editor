/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm



import java.io.File
import org.apache.commons.io.FileUtils
import org.brijest.bufferz._
import org.brijest.bufferz.shells._
import engine._
import engine.model.{World, Player}



object Initializer {
  
  import Config._
  import impl._
  
  private def unpackNativeLibs() {
    val os = sys.props("os.name")
    val natstr = os match {
      case "Linux" => "linux-i586"
      case "Mac OS X" => "macosx-universal"
      case os => sys.error("Unknown OS: " + os)
    }
    val natext = os match {
      case "Linux" => "so"
      case "Mac OS X" => "jnilib"
      case os => sys.error("Unknown OS: " + os)
    }
    
    val candidates = for {
      jarfile <- new File("lib/").listFiles
      if jarfile.getName.endsWith(".jar") && jarfile.getName.contains(natstr)
    } {
      val archive = new java.util.zip.ZipFile(jarfile)
      val entries = archive.entries
      while (entries.hasMoreElements) {
        val entry = entries.nextElement()
        if (entry.getName.contains(natext)) {
          FileUtils.copyInputStreamToFile(archive.getInputStream(entry), new File("lib/" + entry.getName))
        }
      }
    }
  }
  
  def default() {
    // unpack gluegen-rt and jogl native libs
    unpackNativeLibs()
    
    // set java library path
    app.sys.props("java.library.path") = "lib/" + java.io.File.pathSeparator + app.sys.props("java.library.path")
  }
  
  def apply(config: Config): Client = {
    // setup debug info
    config.logging match {
      case Some(logging.screen) =>
        import java.util.logging._
        LogManager.getLogManager.readConfiguration(javaLoggingScreen)
      case _ => 
    }
    
    // setup ui
    val ui = createUI(config)
    
    // setup engine
    val ng = config.engine match {
      case engine.local => new local.LocalEngine(config, Player.default(model.defaultPlayerId), createWorld(config))
      case e => exit("Engine '%s' not recognized.".format(e))
    }
    
    // start engine
    ng.listen(ui)
    ui.engine = Some(ng)
    ng.start()
    
    new Client(ng, ui)
  }
  
  private def createUI(config: Config): UI = config.ui match {
    case ui.swingConsole => new gui.console.ConsoleUI(new SwingStandaloneShell(app.name) with Buffers)
    case ui.swingIso => new gui.iso.SwingIsoUI(app.name)
    case e => exit("User interface '%s' not recognized".format(e))
  }
  
  private def createWorld(config: Config): World = config.world match {
    case None => new World.DefaultWorld
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


name := "storm-enroute"

version := "0.1"

scalaVersion := "2.9.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xexperimental"
)

libraryDependencies += "org.scala-tools.testing" %% "scalacheck" % "1.9"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1"

libraryDependencies += "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.7"

libraryDependencies += "org.slf4j" % "slf4j-jdk14" % "1.6.1"

libraryDependencies += "com.h2database" % "h2" % "1.2.147"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.9.1"

libraryDependencies += "commons-lang" % "commons-lang" % "2.2"

libraryDependencies += "commons-io" % "commons-io" % "2.3"

resolvers += MavenRepository("jogamp", "http://jogamp.org/deployment/maven")

libraryDependencies += "org.jogamp.jogl" % "jogl-all" % "2.0-rc9"

libraryDependencies += "org.jogamp.gluegen" % "gluegen-rt" % "2.0-rc9"

libraryDependencies ++= {
  val os = sys.props("os.name") match {
    case "Linux" => "linux"
    case "Mac OS X" => "macosx"
    case os if os.startsWith("Windows") => "windows"
    case os => sys.error("Cannot obtain lib for OS: " + os)
  }
  val arch = if (os == "macosx") "universal" else sys.props("os.arch") match {
    case "amd64" => "amd64"
    case "i386" => "i586"
    case "x86" => "i586"
    case arch => sys.error("Cannot obtain lib for arch: " + arch)
  }
  val vers = "2.0-rc9"
  val suff = "-natives-" + os + "-" + arch
  val jogampurl = "http://jogamp.org/deployment/maven/org/jogamp/"
  val joglurl = jogampurl + "jogl/jogl-all/" + vers + "/" + "jogl-all-" + vers + suff + ".jar"
  val jogl = "org.jogamp.jogl" % ("jogl-all" + suff) % (vers) from joglurl
  val glueurl = jogampurl + "gluegen/gluegen-rt/" + vers + "/" + "gluegen-rt-" + vers + suff + ".jar"
  val glue = "org.jogamp.gluegen" % ("gluegen-rt" + suff) % (vers) from glueurl
  Seq(jogl, glue)
}

resolvers += "swt-repo" at "https://swt-repo.googlecode.com/svn/repo/"

libraryDependencies += {
  println(sys.props("os.arch"))
  val os = (sys.props("os.name"), sys.props("os.arch")) match {
    case ("Linux", _) => "gtk.linux.x86"
    case ("Mac OS X", "amd64" | "x86_64") => "cocoa.macosx.x86_64"
    case ("Mac OS X", _) => "cocoa.macosx.x86"
    case (os, "amd64") if os.startsWith("Windows") => "win32.win32.x86_64"
    case (os, _) if os.startsWith("Windows") => "win32.win32.x86"
    case (os, arch) => sys.error("Cannot obtain lib for OS '" + os + "' and architecture '" + arch + "'")
  }
  val artifact = "org.eclipse.swt." + os
  "org.eclipse.swt" % artifact % "3.8"
}
            

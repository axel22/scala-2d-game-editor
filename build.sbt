
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

// libraryDependencies += "org.jogamp.jogl" % "jogl-all" % "2.0-rc9"

// libraryDependencies += "org.jogamp.gluegen" % "gluegen-rt" % "2.0-rc9"

libraryDependencies ++= {
  val os = sys.props("os.name") match {
    case "Linux" => "linux"
    case "Mac OS X" => "macosx"
    case os if os.startsWith("Windows") => "windows"
    case os => sys.error("Cannot obtain lib for OS: " + os)
  }
  val arch = if (os == "macosx") "universal" else sys.props("os.name") match {
    case "amd64" => "amd64"
    case "i586" => "i586"
    case arch => sys.error("Cannot obtain lib for arch: " + arch)
  }
  val art = "/natives-" + os + "-" + arch
  Seq(
    "org.jogamp.jogl" % "jogl-all" % "2.0-rc10",
    "org.jogamp.gluegen" % "gluegen-rt" % "2.0-rc10"
  )
}

// old jogl version

// libraryDependencies += "net.java.dev.jogl" % "jogl" % "1.1.1-rc6"

// libraryDependencies += {
//   sys.props("os.name") match {
//     case "Linux" => "net.java.dev.jogl" % "jogl-linux-i586" % "1.1.1-rc6"
//     case "Mac OS X" => "net.java.dev.jogl" % "jogl-macosx-universal" % "1.1.1-rc6"
//     case os if os.startsWith("Windows") => "net.java.dev.jogl" % "jogl-windows-i586" % "1.1.1-rc6"
//     case os => sys.error("Cannot build for OS: " + os)
//   }
// }

// libraryDependencies += "net.java.dev.gluegen" % "gluegen-rt" % "1.0b05"

// libraryDependencies += {
//   sys.props("os.name") match {
//     case "Linux" => "net.java.dev.gluegen" % "gluegen-rt-linux-i586" % "1.0b05"
//     case "Mac OS X" => "net.java.dev.gluegen" % "gluegen-rt-macosx-universal" % "1.0b05"
//     case os if os.startsWith("Windows") => "net.java.dev.gluegen" % "gluegen-rt-windows-i586" % "1.0b05"
//     case os => sys.error("Cannot build for OS: " + os)
//   }
// }


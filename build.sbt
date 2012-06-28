
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

libraryDependencies += "commons-io" % "commons-io" % "1.3.2"

libraryDependencies += "net.java.dev.jogl" % "jogl" % "1.1.1-rc6"

libraryDependencies += {
  sys.props("os.name") match {
    // "net.java.dev.jogl" % "jogl-windows-i586" % "1.1.1-rc6"
    // "net.java.dev.jogl" % "jogl-linux-i586" % "1.1.1-rc6"
    case "Mac OS X" => "net.java.dev.jogl" % "jogl-macosx-universal" % "1.1.1-rc6"
    case os => sys.error("Cannot build for OS: " + os)
  }
}


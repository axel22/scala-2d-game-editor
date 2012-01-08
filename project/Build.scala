/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                          Storm Enroute (c) 2011        **
\*                                          www.storm-enroute.com         */




import sbt._
import Keys._



object StormEnrouteBuild extends Build {
  
  lazy val root = Project("storm-enroute", file(".")) dependsOn (
    RootProject(uri("git://github.com/axel22/mempool.git#HEAD")),
    RootProject(uri("git://git.assembla.com/bufferz.git#HEAD"))
  )
  
}

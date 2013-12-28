package org.brijest.storm



import org.github.scopt._



abstract class DefaultParser(appname: String) extends OptionParser(appname) {
  opt("Srender.outline", "A boolean indicating whether to draw an outline only", { v: String => app.render.outline(v.toBoolean) })
  opt("Srender.shadows", "A boolean indicating whether to draw shadows", { v: String => app.render.shadows(v.toBoolean) })
}



/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.tools



import org.github.scopt._
import com.sun.imageio.plugins.png._
import java.io._
import javax.imageio._



object PngText {
  
  def main(args: Array[String]) {
    val config = new ToolConfigParser()
    
    if (config.parse(args)) {
      pngText(config)
    }
  }
  
  class ToolConfigParser() extends OptionParser("png-text") {
    var key: String = ""
    var value: String = ""
    var filename: String = ""
    var print: Boolean = false
    
    help("h", "help", "Show this help message")
    opt("key", "The name of the text chunk key to add.", { v: String => key = v })
    opt("value", "The value of the text chunk to add.", { v: String => value = v })
    opt("print", "If true, only print the text chunks in the png file.", { v: String => print = v.toBoolean })
    arg("<filename>", "The name of the png file to embed the text chunk into.", { v: String => filename = v })
  }
  
  def pngText(config: ToolConfigParser) {
    if (!config.print) embed(config)
    else printchunk(config)
  }
  
  def printchunk(config: ToolConfigParser) {
    val pngimage = new com.sixlegs.png.PngImage()
    val image = pngimage.read(new java.io.FileInputStream(config.filename), true)
    val dschunk = pngimage.getTextChunk(config.key)
    if (dschunk != null) println(dschunk.getText)
    else println("No tEXt chunk named %s!".format(config.key))
  }
  
  def embed(config: ToolConfigParser) {
    val pngreader = new PNGImageReader(new PNGImageReaderSpi())
    val fis = new stream.FileImageInputStream(new File(config.filename))
    pngreader.setInput(fis, false, false)
    val image = pngreader.read(0, pngreader.getDefaultReadParam)
    
    // Create & populate metadata
    val metadata = new PNGMetadata()
    metadata.tEXt_keyword.add(config.key)
    metadata.tEXt_text.add(config.value)
    
    // Render the PNG to memory
    val pngwriter = new PNGImageWriter(new PNGImageWriterSpi())
    val iioImage = new IIOImage(image, null, null)
    iioImage.setMetadata(metadata) 
    fis.close()
    pngwriter.setOutput(new stream.FileImageOutputStream(new File(config.filename)))
    pngwriter.write(null, iioImage, null)
  }
  
}





/*    ______________  ___  __  ___  _____  _____  ____  __  ____________  *\
**   / __/_  __/ __ \/ _ \/  |/  / / __/ |/ / _ \/ __ \/ / / /_  __/ __/  **
**  _\ \  / / / /_/ / , _/ /|_/ / / _//    / , _/ /_/ / /_/ / / / / _/    **
** /___/ /_/  \____/_/|_/_/  /_/ /___/_/|_/_/|_|\____/\____/ /_/ /___/    **
**                                                                        **
**                                            Storm Enroute (c) 2011      **
\*                                            www.storm-enroute.com       */

package org.brijest.storm.engine
package gui



import java.awt.Image
import java.awt.image.BufferedImage
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import iso._
import model._



class IsoCanvasTests extends WordSpec with ShouldMatchers {
  
  "IsoCanvas" should {
    
    class TestIsoCanvas extends IsoCanvas(24) {
      val img = new BufferedImage(1280, 800, BufferedImage.TYPE_4BYTE_ABGR)
      
      class TestDrawingAdapter extends ImageDrawAdapter(img) with DrawAdapter
      
      drawing.background = false
      drawing.indices = false
      
      type Img = java.awt.Image
      def palette = new DefaultSwingPalette
      def pos = (0, 0);
      def width = img.getWidth
      def height = img.getHeight
      def imageFromPngStream(stream: java.io.InputStream) = javax.imageio.ImageIO.read(stream)
    }
    
    def equalImages(a: BufferedImage, b: BufferedImage): Boolean = {
      val abuff = a.getData.getDataBuffer
      val bbuff = b.getData.getDataBuffer
      
      if (abuff.size != bbuff.size) false else {
        var i = 0
        while (i < abuff.size) {
          if (abuff.getElem(i) != bbuff.getElem(i)) return false
          i += 1
        }
        true
      }
    }
    
    def save(name: String, img: BufferedImage) {
      javax.imageio.ImageIO.write(img, "png", new java.io.File("tmp/" + name + ".png"))
    }
    
    def testAreaDisplay(area: AreaView, picname: String) {
      val canvas = new TestIsoCanvas
      
      canvas.redraw(area, IdleEngine, new canvas.TestDrawingAdapter())
      
      val result = canvas.img
      save(picname, result)
      val expected = canvas.imageFromPngStream(pngStream(picname))
      equalImages(result, expected) should equal (true)
    }
    
    "correctly display area: empty dungeon test 1" in {
      testAreaDisplay(Area.emptyDungeonTest1(16, 16), "dungeon1")
    }

    "correctly display area: empty dungeon test 2" in {
      testAreaDisplay(Area.emptyDungeonTest2(16, 16), "dungeon2")
    }
    
    "correctly display area: empty dungeon test 3" in {
      testAreaDisplay(Area.emptyDungeonTest3(16, 16), "dungeon3")
    }
    
    "correctly display area: empty dungeon test 4" in {
      testAreaDisplay(Area.emptyDungeonTest4(16, 16), "dungeon4")
    }
  }
  
}









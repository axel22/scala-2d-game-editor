package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.h2.jdbc._
import java.io.File
import model.{Area, AreaId}


class Database(config: Config) {
  
  val filename = "%s/%s".format(config.basedir, config.savename)
  val existing = (new File(filename)).exists
  val url = "jdbc:h2:%s;FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192".format(filename)
  Class.forName("org.h2.Driver")
  val conn = java.sql.DriverManager.getConnection(url)
  
  import Database._
  
  def update(s: String) {
    val stmt = conn.createStatement()
    stmt.executeUpdate(s)
  }
  
  def query(s: String) = {
    val stmt = conn.createStatement()
    stmt.executeQuery(s)
  }
  
  if (!existing) {
    update("create database %s".format(Database.name))
    update("create table %s (%s bigint not null, %s blob, primary key (%s))".format(tablename, areaid, data, areaid))
  }
  
  def getArea(id: AreaId) {
    val rs = query("select %s, %s from %s where %s = %d".format(areaid, data, tablename, areaid, id))
    if (!rs.next()) error("Nonexisting area: %d".format(id))
    else {
      val blob = rs.getBlob(data)
      // TODO use blob stream to construct an area
    }
  }
  
  def putArea(id: AreaId, area: Area) {
    // TODO create blob from the area
    update("delete from %s where %s = %d".format(tablename, areaid, id))
    val pstmt = conn.prepareStatement("insert into %s values (%d, ?)".format(tablename, areaid))
    pstmt.setBinaryStream(1, null)
    pstmt.execute()
  }
  
  def terminate() {
    conn.close()
  }
  
}


object Database {
  val name = "save"
  val tablename = "areas"
  val areaid = "id"
  val data = "data"
}


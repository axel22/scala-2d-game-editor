package org.brijest.storm
package engine
package impl.local



import com.weiglewilczek.slf4s._
import org.h2.jdbc._
import java.io.File
import model.{Area, AreaId}
import Simulators.State


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
    update("create table %s (%s bigint not null, %s blob, %s blob, primary key (%s))".format(tablename, areaid, data, statedata, areaid))
  }
  
  def getInfo(id: AreaId): Option[(Area, State)] = {
    val rs = query("select %s, %s, %s from %s where %s = %d".format(areaid, data, statedata, tablename, areaid, id))
    if (!rs.next()) None
    else {
      def deser[T](blob: java.sql.Blob) = {
        val bs = blob.getBinaryStream()
        val ois = new java.io.ObjectInputStream(bs)
        val obj = ois.readObject().asInstanceOf[Area]
        ois.close()
        obj.asInstanceOf[T]
      }
      
      val datablob = rs.getBlob(data)
      val statedatablob = rs.getBlob(statedata)
      Some(deser[Area](datablob), deser[State](statedatablob))
    }
  }
  
  def putInfo(id: AreaId, area: Area, state: State) {
    val bs = new java.io.ByteArrayOutputStream()
    val oos = new java.io.ObjectOutputStream(bs)
    oos.writeObject(area)
    
    update("delete from %s where %s = %d".format(tablename, areaid, id))
    val pstmt = conn.prepareStatement("insert into %s values (%d, ?)".format(tablename, areaid))
    pstmt.setBinaryStream(1, new java.io.ByteArrayInputStream(bs.toByteArray))
    pstmt.execute()
    oos.close()
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
  val statedata = "statedata"
}


package org.brijest.storm.engine
package model



import collection._



trait Plane {
  def name: String
  def size: Int
  def details = "size: %d".format(size)
}

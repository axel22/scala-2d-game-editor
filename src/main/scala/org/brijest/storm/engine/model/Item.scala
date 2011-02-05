package org.brijest.storm
package engine
package model



import org.triggerspace._



trait ItemView extends Trait


abstract class Item(i: EntityId, ii: InstInfo)
extends Entity[Item](i, ii) with ItemView {
  
}

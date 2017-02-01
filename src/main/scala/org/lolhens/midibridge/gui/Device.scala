package org.lolhens.midibridge.gui

import scalafx.beans.property.StringProperty

/**
  * Created by pierr on 01.02.2017.
  */
case class Device(name: String) {
  val nameProperty = new StringProperty(this, "name", name)
}

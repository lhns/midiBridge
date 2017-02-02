package org.lolhens.midibridge.gui

import org.lolhens.midibridge.midi.{MidiLink, MidiSystem}

/**
  * Created by pierr on 02.02.2017.
  */
class LinkManager(midiSystem: MidiSystem) {
  var oldLinks: List[MidiLink] = Nil

  def setLinks(links: Seq[(Device, Device)]): Unit = {
    oldLinks.foreach(_.close())

    oldLinks = links.toList.flatMap {
      case (transmitter, receiver) =>
        val t = midiSystem.devices.filter(_.name == transmitter.name).flatMap(_.transmitter).headOption
        val r = midiSystem.devices.filter(_.name == receiver.name).flatMap(_.receiver).headOption

        (t zip r).flatMap {
          case (transmitter, receiver) =>
            transmitter.setReceiver(receiver)
        }
    }
  }

  def getTransmitters: List[Device] =
    midiSystem.devices.filter(_.isTransmitter).map(e => Device(e.name))

  def getReceivers: List[Device] =
    midiSystem.devices.filter(_.isReceiver).map(e => Device(e.name))
}

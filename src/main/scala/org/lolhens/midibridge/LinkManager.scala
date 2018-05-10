package org.lolhens.midibridge

import monix.execution.atomic.Atomic
import org.lolhens.midibridge.gui.Device
import org.lolhens.midibridge.midi.{MidiLink, MidiReceiver, MidiSystem, MidiTransmitter}

/**
  * Created by pierr on 02.02.2017.
  */
class LinkManager(val midiSystem: MidiSystem) {
  private val _links: Atomic[List[MidiLink]] = Atomic(List.empty[MidiLink])

  def getLinks: Seq[(MidiTransmitter, MidiReceiver)] =
    _links.get.map(link => (link.transmitter, link.receiver))

  def setLinks2(links: Seq[(Device, Device)]): Unit = {
    _links.transform { prevLinks =>
      prevLinks.foreach(_.close())

      links.toList.flatMap {
        case (transmitter, receiver) =>
          val t = midiSystem.devices.filter(_.name == transmitter.name).flatMap(_.transmitter).headOption
          val r = midiSystem.devices.filter(_.name == receiver.name).flatMap(_.receiver).headOption

          (t zip r).flatMap {
            case (transmitter, receiver) =>
              transmitter.setReceiver(receiver)
          }
      }
    }
  }

  def transmitter(name: String): Option[MidiTransmitter] =
    midiSystem.devices.filter(_.name == name).flatMap(_.transmitter).headOption

  def receiver(name: String): Option[MidiReceiver] =
    midiSystem.devices.filter(_.name == name).flatMap(_.receiver).headOption

  def setLinks(links: Seq[(MidiTransmitter, MidiReceiver)]): Unit =
    _links.transform { prevLinks =>
      prevLinks.foreach(_.close())

      links.flatMap {
        case (transmitter, receiver) =>
          transmitter.setReceiver(receiver)
      }.toList
    }

  def close(): Unit = setLinks(Seq.empty)

  def getTransmitters: List[Device] =
    midiSystem.devices.filter(_.isTransmitter).map(e => Device(e.name))

  def getReceivers: List[Device] =
    midiSystem.devices.filter(_.isReceiver).map(e => Device(e.name))
}

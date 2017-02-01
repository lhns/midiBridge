package org.lolhens.midibridge.midi

import javax.sound.{midi => jmidi}

/**
  * Created by pierr on 01.02.2017.
  */
class MidiSystem() {
  def devices: List[MidiDevice] = jmidi.MidiSystem.getMidiDeviceInfo.map(MidiDevice).toList

  @volatile private var openDevices = List[MidiDevice]()

  private[midi] def register(device: MidiDevice) = synchronized {

  }

  def close(): Unit = synchronized {
    openDevices.foreach(_.close())
  }
}

object MidiSystem {
  def apply(): MidiSystem = new MidiSystem()
}

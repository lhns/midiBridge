package org.lolhens.midibridge.midi

import javax.sound.midi.MidiUnavailableException
import javax.sound.{midi => jmidi}

/**
  * Created by pierr on 01.02.2017.
  */
class MidiTransmitter(val device: MidiDevice) {
  def ports: Int = device.self.getMaxTransmitters

  private def transmitter: Option[jmidi.Transmitter] = try {
    Some(device.self.getTransmitter)
  } catch {
    case _: MidiUnavailableException => None
  }

  def setReceiver(receiver: MidiReceiver): Option[MidiLink] = {
    new MidiLink(this, receiver)
  }


}

package org.lolhens.midibridge.midi

/**
  * Created by pierr on 01.02.2017.
  */
class MidiLink(val transmitter: MidiTransmitter, val receiver: MidiReceiver) {
  def close(): Unit = ???
}

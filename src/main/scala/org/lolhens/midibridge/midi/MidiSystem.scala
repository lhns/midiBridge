package org.lolhens.midibridge.midi

import javax.sound.{midi => jmidi}

/**
  * Created by pierr on 01.02.2017.
  */
class MidiSystem() {
  def devices: List[MidiDevice] = jmidi.MidiSystem.getMidiDeviceInfo.map(MidiDevice(_)).toList
}

object MidiSystem {
  def apply(): MidiSystem = new MidiSystem()
}

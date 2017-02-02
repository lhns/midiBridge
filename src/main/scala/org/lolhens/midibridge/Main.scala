package org.lolhens.midibridge

import org.lolhens.midibridge.gui.MidiBridgeGui
import org.lolhens.midibridge.midi.MidiSystem

//import javax.sound.midi._

/**
  * Created by pierr on 24.01.2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val midiSystem = MidiSystem()

    new MidiBridgeGui(midiSystem).main(args)
  }
}

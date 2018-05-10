package org.lolhens.midibridge

import java.io.File
import java.nio.file.Paths

import org.lolhens.midibridge.gui.MidiBridgeGui
import org.lolhens.midibridge.midi.MidiSystem

//import javax.sound.midi._

/**
  * Created by pierr on 24.01.2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val midiSystem = MidiSystem()

    val propertiesFile =
      Paths.get(new File(getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath).getPath)
        .getParent
        .resolve(".midi-links.txt")

    val linkManager = new PersistedLinkManager(midiSystem, propertiesFile)

    args.lift(0).getOrElse("") match {
      case "--help" =>
        println(
          """Options:
            |  use --headless to run without gui""".stripMargin
        )

      case "--headless" =>

      case _ =>
        new MidiBridgeGui(linkManager).main(args)
    }
  }
}

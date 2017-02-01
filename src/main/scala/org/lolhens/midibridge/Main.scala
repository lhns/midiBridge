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

    val devices = midiSystem.devices.groupBy(_.name)

    devices.flatMap(_._2).foreach { device =>
      println(device.name)
      println("-> " + device.description + " t:" + device.isTransmitter + " r:" + device.isReceiver)
      //println("-> " + deviceInfo.getVendor)
      //val device = MidiSystem.getMidiDevice(deviceInfo)
      //if (!device.isOpen) device.open()
      //device.getTransmitter.setReceiver(device.getReceiver)
    }

    val midiLink = devices("Komplete Audio 6 MIDI" /*"APC MINI"*/).flatMap(_.transmitter).head.setReceiver(
      devices(/*"loopMIDI Port 1"*/ "LoopBe Internal MIDI").flatMap(_.receiver).head)

    midiLink.foreach(_.close())

    println(devices("Komplete Audio 6 MIDI" /*"APC MINI"*/).flatMap(_.transmitter).head.device)

    //println(link.isDefined)

    /*while (true) {
      Thread.sleep(1000)
    }*/

    new MidiBridgeGui().main(args)
  }

  /*implicit class RichDeviceInfo(val deviceInfo: MidiDevice.Info) extends AnyVal {
    def device: MidiDevice = {
      val device = MidiSystem.getMidiDevice(deviceInfo)
      device
    }

    def openDevice: MidiDevice = {
      val device = deviceInfo.device
      if (!device.isOpen) device.open()
      device
    }

    def isTransmitter: Boolean = deviceInfo.device.getMaxTransmitters != 0

    def isReceiver: Boolean = deviceInfo.device.getMaxReceivers != 0
  }*/

}

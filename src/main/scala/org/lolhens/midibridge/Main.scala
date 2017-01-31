package org.lolhens.midibridge

import javax.sound.midi._

/**
  * Created by pierr on 24.01.2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val deviceInfos = MidiSystem.getMidiDeviceInfo.toList.groupBy(_.getName)

    deviceInfos.flatMap(_._2).foreach { deviceInfo =>
      println(deviceInfo.getName)
      println("-> " + deviceInfo.getDescription)
      //println("-> " + deviceInfo.getVendor)
      //val device = MidiSystem.getMidiDevice(deviceInfo)
      //if (!device.isOpen) device.open()
      //device.getTransmitter.setReceiver(device.getReceiver)
    }

    deviceInfos("Komplete Audio 6 MIDI" /*"APC MINI"*/)(0).openDevice.getTransmitter.setReceiver(
      deviceInfos(/*"loopMIDI Port 1"*/ "LoopBe Internal MIDI")(1).openDevice.getReceiver)

    while (true) {
      Thread.sleep(1000)
    }
  }

  implicit class RichDeviceInfo(val deviceInfo: MidiDevice.Info) extends AnyVal {
    def openDevice: MidiDevice = {
      val device = MidiSystem.getMidiDevice(deviceInfo)
      if (!device.isOpen) device.open()
      device
    }
  }

}

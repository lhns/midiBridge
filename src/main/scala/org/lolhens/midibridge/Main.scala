package org.lolhens.midibridge

import javax.sound.midi._

/**
  * Created by pierr on 24.01.2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val deviceInfos = MidiSystem.getMidiDeviceInfo().toList

    deviceInfos.foreach { deviceInfo =>
      println(deviceInfo.getName)
      println("-> " + deviceInfo.getDescription)
      //println("-> " + deviceInfo.getVendor)
      //val device = MidiSystem.getMidiDevice(deviceInfo)
      //if (!device.isOpen) device.open()
      //device.getTransmitter.setReceiver(device.getReceiver)
    }

    def device(info: List[MidiDevice.Info], name: String): List[MidiDevice] = info.filter(_.getName == name).map(MidiSystem.getMidiDevice)

    device(deviceInfos, "APC MINI")(0).getTransmitter.setReceiver(device(deviceInfos, "loopMIDI Port 1" /*"LoopBe Internal MIDI"*/)(1).getReceiver)

    while (true) {
      Thread.sleep(1000)
    }
  }
}

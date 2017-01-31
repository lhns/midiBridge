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
      println("-> " + deviceInfo.getDescription + " t:" + deviceInfo.isTransmitter + " r:" + deviceInfo.isReceiver)
      //println("-> " + deviceInfo.getVendor)
      //val device = MidiSystem.getMidiDevice(deviceInfo)
      //if (!device.isOpen) device.open()
      //device.getTransmitter.setReceiver(device.getReceiver)
    }

    deviceInfos("Komplete Audio 6 MIDI" /*"APC MINI"*/).filter(_.isTransmitter).head.openDevice.getTransmitter.setReceiver(
      deviceInfos(/*"loopMIDI Port 1"*/ "LoopBe Internal MIDI").filter(_.isReceiver).head.openDevice.getReceiver)

    while (true) {
      Thread.sleep(1000)
    }
  }

  implicit class RichDeviceInfo(val deviceInfo: MidiDevice.Info) extends AnyVal {
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
  }

}

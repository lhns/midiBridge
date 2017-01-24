import javax.sound.midi.MidiSystem

/**
  * Created by pierr on 24.01.2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    MidiSystem.getMidiDeviceInfo.foreach{deviceInfo =>
      println(deviceInfo.getName)
      println(deviceInfo.getDescription)
      val device = MidiSystem.getMidiDevice(deviceInfo)
      if (!device.isOpen) device.open()
      device.getTransmitter.setReceiver(device.getReceiver)
  }
  }
}

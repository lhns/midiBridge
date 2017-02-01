package org.lolhens.midibridge.midi

import javax.sound.midi.MidiUnavailableException
import javax.sound.{midi => jmidi}

/**
  * Created by pierr on 01.02.2017.
  */
class MidiTransmitter(val device: MidiDevice) {
  def ports: Int = device.self.getMaxTransmitters

  private[midi] def open(): Option[MidiTransmitter.Handle] = {
    val deviceHandle = device.open()
    try {
      Some(new MidiTransmitter.Handle(this, deviceHandle, device.self.getTransmitter))
    } catch {
      case _: MidiUnavailableException =>
        deviceHandle.close()
        None
    }
  }

  def setReceiver(receiver: MidiReceiver): Option[MidiLink] = {
    val transmitterHandle = this.open()
    val receiverHandle = receiver.open()

    (transmitterHandle zip receiverHandle).headOption.map { handles =>
      new MidiLink(handles._1, handles._2)
    }
  }
}

object MidiTransmitter {

  class Handle(val transmitter: MidiTransmitter, deviceHandle: MidiDevice.Handle, _self: jmidi.Transmitter) {
    @volatile private var _closed = false

    def self: jmidi.Transmitter =
      if (_closed)
        throw new IllegalStateException("Handle already closed")
      else
        _self

    def close(): Unit = {
      _closed = true
      _self.close()
      deviceHandle.close()
    }

    override def finalize(): Unit = close()
  }

}

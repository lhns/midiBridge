package org.lolhens.midibridge.midi

import javax.sound.midi.{MidiMessage, MidiUnavailableException, Receiver}
import javax.sound.{midi => jmidi}

/**
  * Created by pierr on 01.02.2017.
  */
class MidiReceiver(val device: MidiDevice) {
  def ports: Int = device.self.getMaxReceivers

  private[midi] def open(): Option[MidiReceiver.Handle] = {
    val deviceHandle = device.open()
    try {
      Some(new MidiReceiver.Handle(this, deviceHandle, device.self.getReceiver))
    } catch {
      case _: MidiUnavailableException =>
        deviceHandle.close()
        None
    }
  }

  class Session(handle: MidiReceiver.Handle) {
    def send(message: MidiMessage, timeStamp: Long): Unit = handle.self.send(message, timeStamp)
  }

  def withSession[E](actions: PartialFunction[Option[Session], E]): Option[E] =
    open().map { handle =>
      val result = actions.lift(Some(new Session(handle)))
      handle.close()
      result
    }.getOrElse {
      actions.lift(None)
    }

  def send(message: MidiMessage, timeStamp: Long): Option[Unit] = withSession {
    case Some(session) => session.send(message, timeStamp)
  }
}

object MidiReceiver {

  class Handle(val receiver: MidiReceiver, deviceHandle: MidiDevice.Handle, _self: jmidi.Receiver) {
    @volatile private var _closed = false

    def self: Receiver =
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

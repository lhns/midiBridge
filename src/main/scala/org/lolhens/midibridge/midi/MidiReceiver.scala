package org.lolhens.midibridge.midi

import javax.sound.{midi => jmidi}
import javax.sound.midi.{MidiMessage, MidiUnavailableException}

/**
  * Created by pierr on 01.02.2017.
  */
class MidiReceiver(val device: MidiDevice) {
  def ports: Int = device.self.getMaxReceivers

  private[midi] def open(): Option[Handle] = try {
    Some(new Handle(device.self.getReceiver))
  } catch {
    case _: MidiUnavailableException => None
  }

  class Handle(val receiver: jmidi.Receiver) {
    def close(): Unit = receiver.close()
  }

  class Session {
    def send(message: MidiMessage, timeStamp: Long): Unit = ???
  }

  def session: Option[(Session => Unit) => Unit] = {
    open().map { handle =>

    }
  }

  session.foreach(_.apply {s =>
    println("test")
  })

  def send(message: MidiMessage, timeStamp: Long): Unit = session.map { session =>
    session.send(message, timeStamp)
  }
}

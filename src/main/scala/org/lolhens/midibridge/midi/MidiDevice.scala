package org.lolhens.midibridge.midi

import javax.sound.{midi => jmidi}
import monix.execution.atomic.Atomic

/**
  * Created by pierr on 01.02.2017.
  */
case class MidiDevice(info: jmidi.MidiDevice.Info) {
  def name: String = info.getName

  def description: String = info.getDescription

  def vendor: String = info.getVendor

  def version: String = info.getVersion

  private[midi] lazy val self = jmidi.MidiSystem.getMidiDevice(info)

  def isReceiver: Boolean = self.getMaxReceivers != 0

  def isTransmitter: Boolean = self.getMaxTransmitters != 0

  def receiver: Option[MidiReceiver] =
    if (isReceiver)
      Some(new MidiReceiver(this))
    else
      None

  def transmitter: Option[MidiTransmitter] =
    if (isTransmitter)
      Some(new MidiTransmitter(this))
    else
      None

  private[midi] def open() = new MidiDevice.Handle(this)

  private val openHandles = Atomic(List[MidiDevice.Handle]())
}

object MidiDevice {

  class Handle(val device: MidiDevice) {
    if (device.openHandles.getAndTransform(this +: _).isEmpty)
      device.self.open()

    def close(): Unit =
      if (device.openHandles.transformAndGet(_.filterNot(_ == this)).isEmpty)
        device.self.close()

    override def finalize(): Unit = close()
  }

}

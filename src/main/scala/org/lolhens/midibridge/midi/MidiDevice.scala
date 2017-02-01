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

  def hasReceiver: Boolean = self.getMaxReceivers != 0

  def hasTransmitter: Boolean = self.getMaxTransmitters != 0

  def receiver(implicit midiSystem: MidiSystem): Option[MidiReceiver] =
    if (hasReceiver)
      Some(new MidiReceiver(this))
    else
      None

  def transmitter(implicit midiSystem: MidiSystem): Option[MidiTransmitter] =
    if (hasTransmitter)
      Some(new MidiTransmitter(this))
    else
      None

  private[midi] def open() = new Handle()

  private val openHandles = Atomic(List[Handle]())

  class Handle {
    if (openHandles.getAndTransform(this +: _).isEmpty)
      self.open()

    def close(): Unit =
      if (openHandles.transformAndGet(_.filterNot(_ == this)).isEmpty)
        self.close()

    override def finalize(): Unit = close()
  }

}

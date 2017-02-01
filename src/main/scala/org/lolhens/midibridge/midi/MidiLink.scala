package org.lolhens.midibridge.midi

/**
  * Created by pierr on 01.02.2017.
  */
class MidiLink(transmitterHandle: MidiTransmitter.Handle, receiverHandle: MidiReceiver.Handle) {
  transmitterHandle.self.setReceiver(receiverHandle.self)

  def receiver: MidiReceiver = receiverHandle.receiver

  def transmitter: MidiTransmitter = transmitterHandle.transmitter

  def close(): Unit = {
    transmitterHandle.close()
    receiverHandle.close()
  }
}

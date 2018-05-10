package org.lolhens.midibridge

import java.nio.file._

import org.lolhens.midibridge.midi.{MidiReceiver, MidiSystem, MidiTransmitter}

import scala.collection.JavaConverters._

class PersistedLinkManager(midiSystem: MidiSystem,
                           propertiesFile: Path) extends LinkManager(midiSystem: MidiSystem) {
  def readLinks(path: Path): Seq[(MidiTransmitter, MidiReceiver)] =
    if (Files.exists(path))
      Files.readAllLines(path).asScala.toList.flatMap { linkString =>
        val Seq(transmitterString, receiverString, _*) = linkString.split(";", -1).toList :+ ""
        val linkOption = for {
          transmitter <- transmitter(transmitterString)
          receiver <- receiver(receiverString)
        } yield transmitter -> receiver
        linkOption.toSeq
      }
    else Seq.empty

  def writeLinks(path: Path, links: Seq[(MidiTransmitter, MidiReceiver)]): Unit = {
    val lines: Seq[String] = links.map {
      case (transmitter, receiver) =>
        s"${transmitter.device.name};${receiver.device.name}"
    }

    Files.write(path, lines.asJava, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  super.setLinks(readLinks(propertiesFile))

  override def setLinks(links: Seq[(MidiTransmitter, MidiReceiver)]): Unit = {
    super.setLinks(links)
    writeLinks(propertiesFile, links)
  }

  override def close(): Unit = super.setLinks(Seq.empty)
}

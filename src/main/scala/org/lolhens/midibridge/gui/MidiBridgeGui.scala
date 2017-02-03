package org.lolhens.midibridge.gui

import org.lolhens.midibridge.midi.MidiSystem
import org.tbee.javafx.scene.layout.MigPane

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout._

/**
  * Created by pierr on 01.02.2017.
  */
class MidiBridgeGui(midiSystem: MidiSystem) extends JFXApp {
  val linkManager = new LinkManager(midiSystem)

  val tableTransmitters = new TableView[Device] {
    columns += new TableColumn[Device, String] {
      text = "Transmitter"
      cellValueFactory = _.value.nameProperty
      prefWidth = 180
    }
    items = ObservableBuffer(linkManager.getTransmitters: _*)
  }

  val tableReceivers = new TableView[Device] {
    columns += new TableColumn[Device, String] {
      text = "Receiver"
      cellValueFactory = _.value.nameProperty
      prefWidth = 180
    }
    items = ObservableBuffer(linkManager.getReceivers: _*)
  }

  val tableLinks = new TableView[(Device, Device)] {
    columns ++= Seq(
      new TableColumn[(Device, Device), String] {
        text = "Transmitter"
        cellValueFactory = e => new StringProperty(this, "Transmitter", e.value._1.name)
        prefWidth = 180
      },
      new TableColumn[(Device, Device), String] {
        text = "Receiver"
        cellValueFactory = e => new StringProperty(this, "Receiver", e.value._2.name)
        prefWidth = 180
      }
    )
    items = ObservableBuffer()
  }

  val buttonAdd = new Button("+")
  val buttonRemove = new Button("-")
  val buttonApply = new Button("Apply")

  buttonAdd.onAction = handle {
    val transmitter = tableTransmitters.selectionModel.value.selectedItems.headOption
    val receiver = tableReceivers.selectionModel.value.selectedItems.headOption
    (transmitter zip receiver).headOption.foreach {
      case (transmitter, receiver) =>
        val link = transmitter -> receiver
        tableLinks.items.value.add(link)
    }
  }

  buttonRemove.onAction = handle {
    tableLinks.selectionModel.value.selectedItems.headOption.foreach { selectedItem =>
      tableLinks.items.value.remove(selectedItem)
    }
  }

  buttonApply.onAction = handle {
    linkManager.setLinks(tableLinks.items.value.toList)

    tableTransmitters.items = ObservableBuffer(linkManager.getTransmitters: _*)
    tableReceivers.items = ObservableBuffer(linkManager.getReceivers: _*)

    println("Links applied!")
  }

  stage = new PrimaryStage {
    title.value = "Midi Bridge"

    width = 500
    height = 400

    scene = new Scene {
      root = new Pane(new MigPane(
        "insets 2",
        "[grow][grow]",
        "[grow][26::][grow]"
      ) {
        add(tableTransmitters, "cell 0 0, grow")
        add(tableReceivers, "cell 1 0, grow")
        add(new MigPane("insets 0", "[30::][30::][grow]", "[grow]") {
          add(buttonAdd, "cell 0 0, grow")
          add(buttonRemove, "cell 1 0, grow")
          add(buttonApply, "cell 2 0, grow")
        }, "cell 0 1 2 1, grow")
        add(tableLinks, "cell 0 2 2 1, grow")
      })
    }

    onHiding = handle {
      println("Closing links")
      linkManager.setLinks(Nil)
    }
  }
}

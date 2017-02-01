package org.lolhens.midibridge.gui

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout._

/**
  * Created by pierr on 01.02.2017.
  */
class MidiBridgeGui extends JFXApp {
  val transmitters = new TableView[Device] {
    columns += new TableColumn[Device, String] {
      text = "Transmitter"
      cellValueFactory = _.value.nameProperty
      prefWidth = 180
    }
    items = ObservableBuffer(Device("asdf"))
  }

  val receivers = new TableView[Device] {
    columns += new TableColumn[Device, String] {
      text = "Receiver"
      cellValueFactory = _.value.nameProperty
      prefWidth = 180
    }
    items = ObservableBuffer(Device("asdf"))
  }

  val links = new TableView[(Device, Device)] {
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
    items = ObservableBuffer(Device("asdf") -> Device("fdadas"))
  }

  stage = new PrimaryStage {
    title.value = "TEST"

    width = 800
    height = 600

    scene = new Scene {
      root = new GridPane {
        add(transmitters, 0, 0, 1, 1)
        add(receivers, 1, 0, 1, 1)
        add(links, 0, 1, 2, 1)

        val constraints: (Seq[RowConstraints], Seq[ColumnConstraints]) = {
          import scala.collection.JavaConversions._
          val managedChildren = children.toList.filter(_.isManaged)
          val numRows = managedChildren.map(e => javafx.scene.layout.GridPane.getRowIndex(e) + javafx.scene.layout.GridPane.getRowSpan(e)).max
          val numCols = managedChildren.map(e => javafx.scene.layout.GridPane.getColumnIndex(e) + javafx.scene.layout.GridPane.getColumnSpan(e)).max

          (
            (0 until numRows).map(_ => new RowConstraints {
              //hgrow = Priority.Always
              percentHeight = 100
            }),
            (0 until numCols).map(_ => new ColumnConstraints {
              //vgrow = Priority.Always
              percentWidth = 100
            })
          )
        }

        rowConstraints = constraints._1
        columnConstraints = constraints._2
      }
    }
  }
}

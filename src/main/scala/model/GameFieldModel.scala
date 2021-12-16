package model

import displayObjects.GameField

import scala.collection.mutable.ArrayBuffer

case class GameFieldPosition(row: Int, column: Int)

case class GameFieldModel(width: Int, height: Int, graphics: GameField) {
  private val matrix = buildMatrix()

  private def buildMatrix(): ArrayBuffer[ArrayBuffer[Int]] = {
    val buffer = ArrayBuffer[ArrayBuffer[Int]]()

    for(i <- 0 until height) {
      buffer.addOne(ArrayBuffer[Int]())
      for(j <- 0 until width ) {
        buffer(i).addOne(0)
      }
    }

    buffer
  }

  def takePositions(positions: List[GameFieldPosition]): Unit = {
    positions.map(elem => {
      matrix(elem.row)(elem.column) = 1
    })
  }

  def positionsAreFree(positions: List[GameFieldPosition]): Boolean = {
    positions.map(elem => {
      matrix(elem.row)(elem.column)
    }).sum == 0
  }

  def getTakenPositions(): List[GameFieldPosition] = {
    val zippedMatrix = matrix.zipWithIndex
    zippedMatrix.flatMap{ case (row, rowIndex) => {
      val zippedRow = row.zipWithIndex
      for {
        (elem, colIndex) <- zippedRow
        if elem == 1
      } yield GameFieldPosition(rowIndex, colIndex)
    }}.toList
  }

  def removeFilledRows(): Int =  {
    val newMatrix = matrix.foldLeft(ArrayBuffer[ArrayBuffer[Int]]())((acc, row) => {
      if (row.sum < width) {
        acc.addOne(row)
      } else {
        acc
      }
    })

    val diff = matrix.length - newMatrix.length

    for(_ <- 1 to diff) {
      val newRow = ArrayBuffer[Int]()
      for(j <- 0 until width ) {
        newRow.addOne(0)
      }
      newMatrix.prepend(newRow)
    }

    matrix.clear()
    matrix.addAll(newMatrix)

    diff
  }

  def clearAll(): GameFieldModel = {
    GameFieldModel.initial(width, height)

  }
}

object GameFieldModel {
  def initial(
    width: Int,
    height: Int
  ):GameFieldModel =
    GameFieldModel(width, height, GameField(width, height))
}

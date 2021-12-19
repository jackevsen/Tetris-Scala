package jackevsen.model

import indigo.shared.datatypes.Point
import jackevsen.displayObjects.GameField

case class GameFieldPosition(row: Int, column: Int)

case class GameFieldModel(width: Int, height: Int, gameField: GameField, cellsList: List[Int]) {
  def withGameField(newGameField: GameField): GameFieldModel =
    this.copy(gameField = newGameField)

  def withCellsList(newList: List[Int]): GameFieldModel =
    this.copy(cellsList = newList)

  def takePositions(positions: List[GameFieldPosition]): GameFieldModel = {
    val updateIndexesList = positions.map(elem => {
      getCellIndex(elem)
    })

    withCellsList(
      updateIndexesList
        .foldLeft(cellsList)((acc, index) => {
          acc.updated(index, 1)
        })
    )
  }

  def positionsAreFree(positions: List[GameFieldPosition]): Boolean = {
    positions.map(elem => {
      cellsList(getCellIndex(elem))
    }).sum == 0
  }

  def getTakenPositions(): List[GameFieldPosition] = {
    val zippedList = cellsList.zipWithIndex

    for {
      (elem, index) <- zippedList
      if elem == 1
    } yield getGameFieldPosition(index)
  }

  def removeFilledRows(): (Int, GameFieldModel) =  {
    val newCellsList = (0 to height - 1).foldLeft(List[Int]())((acc, index) => {
      val startIndex = index * width
      val row = cellsList.slice(startIndex, startIndex + width)
      if (row.sum < width) {
        acc ::: row
      } else {
        acc
      }
    })

    val diff = cellsList.length - newCellsList.length

    (diff, withCellsList(List.fill(diff)(0) ::: newCellsList))
  }

  def clearAll(): GameFieldModel =
    GameFieldModel.initial(width, height)

  def getCellIndex(position: GameFieldPosition): Int =
    (position.row * width) + position.column

  def getGameFieldPosition(index: Int): GameFieldPosition = {
    GameFieldPosition(
      Math.round(index / width),
      index % width
    )
  }
}

object GameFieldModel {
  def initial(
    width: Int,
    height: Int
  ):GameFieldModel =
    GameFieldModel(
      width = width,
      height = height,
      gameField = GameField(width, height, Point(0, 0)),
      cellsList = List.fill(width * height)(0)
    )
}

package jackevsen.model

case class GameField(width: Int, height: Int, cellsList: List[Boolean]) {
  def withCellsList(newList: List[Boolean]): GameField =
    this.copy(cellsList = newList)

  def withTakenPositions(positions: List[GameFieldPosition]): GameField = {
    val updateIndexesList = positions.map(elem => {
      getCellIndex(elem)
    })

    withCellsList(
      updateIndexesList
        .foldLeft(cellsList)((acc, index) => {
          acc.updated(index, true)
        })
    )
  }

  def positionsAreFree(positions: List[GameFieldPosition]): Boolean = {
    !positions.map(elem => {
      cellsList(getCellIndex(elem))
    }).contains(true)
  }

  def getTakenPositions(): List[GameFieldPosition] = {
    val zippedList = cellsList.zipWithIndex

    for {
      (filledElem, index) <- zippedList
      if filledElem
    } yield getGameFieldPosition(index)
  }

  def removeFilledRows(): (Int, GameField) =  {
    val newCellsList = (0 to height - 1).foldLeft(List[Boolean]())((acc, index) => {
      val startIndex = index * width
      val row = cellsList.slice(startIndex, startIndex + width)
      if (row.contains(false)) {
        acc ::: row
      } else {
        acc
      }
    })

    val diff = cellsList.length - newCellsList.length

    (diff / width, withCellsList(List.fill(diff)(false) ::: newCellsList))
  }

  def clearAll(): GameField =
    GameField.initial(width, height)

  def getCellIndex(position: GameFieldPosition): Int =
    (position.row * width) + position.column

  def getGameFieldPosition(index: Int): GameFieldPosition = {
    GameFieldPosition(
      Math.round(index / width),
      index % width
    )
  }
}

object GameField {
  def initial(
    width: Int,
    height: Int
  ):GameField =
    GameField(
      width = width,
      height = height,
      cellsList = List.fill(width * height)(false)
    )
}

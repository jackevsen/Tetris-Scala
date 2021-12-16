package model

import displayObjects.GameField
import displayObjects.pieces._
import indigo.shared.BoundaryLocator
import indigo.shared.time.Seconds
import indigo.shared.datatypes.Point
import utils.GameUtils
import indigo.shared.dice.Dice

case class GameModel(
  width: Int,
  height: Int,
  gameFieldModel: GameFieldModel,
  currentPiece: Piece,
  score: Int = 0,
  bestScore: Int = 0,
  movementInterval: Seconds = Seconds(1),
  paused: Boolean = false,
  gameOver: Boolean = false,
  gameStarted: Boolean = false,
  curentDelta: Seconds = Seconds(0),
) {
  def gameField: GameField =
    gameFieldModel.graphics

  def update(delta: Seconds, boundaryLocator: BoundaryLocator, dice: Dice): GameModel = {
    if (!gameStarted || paused || gameOver) {
      return this
    }

    if(curentDelta + delta >= movementInterval) {
      val newPosition = currentPiece.getPosition.withY(
        currentPiece.getPosition.y + currentPiece.cellSize
      )

      updatePosition(newPosition, boundaryLocator, dice)
        .setDelta(Seconds(0))
    } else {
      setDelta(curentDelta + delta)
    }
  }

  def setPaused(value: Boolean): GameModel =
    this.copy(paused = value)

  def setGameOver(value: Boolean): GameModel =
    this.copy(
      gameOver = value,
      gameStarted = false,
      paused = false
    )

  def setCurrentPiece(newPiece: Piece): GameModel =
    this.copy(currentPiece = newPiece)

  def setDelta(value: Seconds): GameModel =
    this.copy(curentDelta = value)

  def setScore(value: Int): GameModel =
    this.copy(score = value)

  def setBestScore(value: Int) =
    this.copy(bestScore = value)

  def setGameStarted(value: Boolean): GameModel =
    this.copy(
      gameStarted = value,
      gameOver = false,
      paused = false,
    )

  def updatePosition(newPosition: Point, boundaryLocator: BoundaryLocator, dice: Dice): GameModel = {
    val prevPosition = currentPiece.getPosition

    GameUtils.setPiecePosition(
      currentPiece,
      newPosition,
      gameField,
      boundaryLocator
    )

    val positionsInGameField = GameUtils.getPieceFragmentsPositions(currentPiece, boundaryLocator)

    val maxRow  = positionsInGameField.foldLeft(0)((acc, elem) => {
      if (elem.row > acc) {
        elem.row
      } else {
        acc
      }
    })

    if (!gameFieldModel.positionsAreFree(positionsInGameField)) {
      currentPiece.setPosition(prevPosition)
      if (newPosition.y != prevPosition.y) {
        gameFieldModel.takePositions(
          GameUtils.getPieceFragmentsPositions(currentPiece, boundaryLocator)
        )
        return processNextPiece(dice, boundaryLocator)
      }
    } else if (maxRow == gameFieldModel.height - 1) {
      gameFieldModel.takePositions(positionsInGameField)
      return processNextPiece(dice, boundaryLocator)
    }

    this
  }

  def setRandomPiece(dice: Dice): GameModel = {
    val nextPiece = GameUtils.getRandomPiece(dice)
    nextPiece.setPosition(Point((gameFieldModel.width / 2) * nextPiece.cellSize, 0))
    this.copy(currentPiece = nextPiece)
  }

  def processNextPiece(dice: Dice, boundaryLocator: BoundaryLocator): GameModel = {
    val removedRowsCount = gameFieldModel.removeFilledRows()
    val scoreForRows = removedRowsCount * (GameUtils.scoreForItem * width)

    var thisCopy = setRandomPiece(dice)
      .setScore(score + scoreForRows)

    if(!gameFieldModel.positionsAreFree(
      GameUtils.getPieceFragmentsPositions(
        thisCopy.currentPiece, boundaryLocator
      ))
    ) {
      thisCopy = thisCopy.setGameOver(true)
      if (score > bestScore) {
        thisCopy.setBestScore(score)
      } else {
        thisCopy
      }
    } else {
      thisCopy
    }
  }

  def startNewGame(): GameModel = {
    val newGameFieldModel = gameFieldModel.clearAll()
    newGameFieldModel.graphics.setPosition(gameField.getPosition)

    setScore(0)
      .copy(gameFieldModel = newGameFieldModel)
      .setRandomPiece(GameUtils.getDice())
      .setGameStarted(true)
  }
}

object GameModel {
  def initial(
    width: Int = 10,
    height: Int = 20
  ): GameModel = {
    val curentPiece = GameUtils.getRandomPiece(GameUtils.getDice())
    curentPiece.setPosition(Point((width / 2) * GameUtils.cellSize, 0))

    GameModel(
      width,
      height,
      GameFieldModel.initial(width, height),
      curentPiece
    )
  }
}

package jackevsen.model

import jackevsen.displayObjects.{GameField, Piece}
import indigo.shared.BoundaryLocator
import indigo.shared.time.Seconds
import indigo.shared.datatypes.Point
import jackevsen.utils.GameUtils
import indigo.shared.dice.Dice

case class Game(
  width: Int,
  height: Int,
  gameFieldModel: GameFieldModel,
  currentPiece: Piece,
  score: Int,
  bestScore: Int,
  movementInterval: Seconds,
  paused: Boolean,
  gameOver: Boolean,
  gameStarted: Boolean,
  curentDelta: Seconds,
) {
  def gameField: GameField =
    gameFieldModel.gameField

  def update(delta: Seconds, boundaryLocator: BoundaryLocator, dice: Dice): Game = {
    if (!gameStarted || paused || gameOver) {
      return this
    }

    if(curentDelta + delta >= movementInterval) {
      val newPosition = currentPiece.position.withY(
        currentPiece.position.y + GameUtils.cellSize
      )

      updatePosition(newPosition, boundaryLocator, dice)
        .withDelta(Seconds(0))
    } else {
      withDelta(curentDelta + delta)
    }
  }

  def withGameFieldModel(newGameFieldModel: GameFieldModel): Game = {
    this.copy(gameFieldModel = newGameFieldModel)
  }

  def withPause(value: Boolean): Game =
    this.copy(paused = value)

  def withGameOver(value: Boolean): Game =
    this.copy(
      gameOver = value,
      gameStarted = false,
      paused = false
    )

  def withCurrentPiece(newPiece: Piece): Game =
    this.copy(currentPiece = newPiece)

  def withDelta(value: Seconds): Game =
    this.copy(curentDelta = value)

  def withScore(value: Int): Game =
    this.copy(score = value)

  def withBestScore(value: Int): Game =
    this.copy(bestScore = value)

  def withGameStarted(value: Boolean): Game =
    this.copy(
      gameStarted = value,
      gameOver = false,
      paused = false,
    )

  def updatePosition(newPosition: Point, boundaryLocator: BoundaryLocator, dice: Dice): Game = {
    val prevPosition = currentPiece.position

    val newPiece = GameUtils.setPiecePosition(
      currentPiece,
      newPosition,
      gameFieldModel,
      boundaryLocator
    )

    val positionsInGameField = GameUtils.getPieceFragmentsPositions(newPiece, boundaryLocator)

    val maxRow  = positionsInGameField.foldLeft(0)((acc, elem) => {
      if (elem.row > acc) {
        elem.row
      } else {
        acc
      }
    })

    if (newPosition.y != prevPosition.y && newPiece.position.y != newPosition.y) {
      return withGameFieldModel(
        gameFieldModel.takePositions(
          GameUtils.getPieceFragmentsPositions(currentPiece, boundaryLocator)
        )
      ).processNextPiece(dice, boundaryLocator)
    } else if (maxRow == gameFieldModel.height - 1) {
      return withGameFieldModel(
        gameFieldModel.takePositions(positionsInGameField)
      ).processNextPiece(dice, boundaryLocator)
    }

    this.withCurrentPiece(newPiece)
  }

  def setRandomPiece(dice: Dice): Game = {
    val nextPiece = GameUtils.getRandomPiece(dice)
      .withPosition(Point((gameFieldModel.width / 2) * GameUtils.cellSize, 0))
    withCurrentPiece(nextPiece)
  }

  def processNextPiece(dice: Dice, boundaryLocator: BoundaryLocator): Game = {
    val (removedRowsCount, newGameFieldModel) = gameFieldModel.removeFilledRows()
    val scoreForRows = removedRowsCount * (GameUtils.scoreForItem * width)

    val thisCopy = setRandomPiece(dice)
      .withScore(score + scoreForRows)
      .withGameFieldModel(newGameFieldModel)

    if(!gameFieldModel.positionsAreFree(
      GameUtils.getPieceFragmentsPositions(
        thisCopy.currentPiece, boundaryLocator
      ))
    ) {
        if (score > bestScore) {
          thisCopy
            .withGameOver(true)
            .withBestScore(score)
        } else {
          thisCopy
            .withGameOver(true)
        }
    } else {
      thisCopy
    }
  }

  def startNewGame(): Game = {
    val newGameFieldModel = gameFieldModel
      .clearAll()
      .withGameField(gameField.withPosition(gameField.position))

    withScore(0)
      .copy(gameFieldModel = newGameFieldModel)
      .setRandomPiece(GameUtils.getDice())
      .withGameStarted(true)
  }
}

object Game {
  def initial(
    width: Int = 10,
    height: Int = 20
  ): Game = {
    val randomPiece = GameUtils.getRandomPiece(GameUtils.getDice())
      .withPosition(Point((width / 2) * GameUtils.cellSize, 0))

    Game(
      width = width,
      height = height,
      gameFieldModel = GameFieldModel.initial(width, height),
      currentPiece = randomPiece,
      score = 0,
      bestScore = 0,
      movementInterval = Seconds(1),
      paused = false,
      gameOver = false,
      gameStarted = false,
      curentDelta = Seconds(0)
    )
  }
}

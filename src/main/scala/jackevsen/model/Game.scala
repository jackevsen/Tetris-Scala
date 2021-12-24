package jackevsen.model

import indigo.shared.datatypes.Point
import jackevsen.utils.GameUtils
import jackevsen.displayObjects.Piece

case class Game(
  gameField: GameField,
  currentPiece: Piece,
  score: Int,
  bestScore: Int,
  movementInterval: Float,
  paused: Boolean,
  gameOver: Boolean,
  gameStarted: Boolean,
  curentDelta: Float,
) {
  def isGameStopped: Boolean =
    !gameStarted || paused || gameOver

  def withGameField(newGameField: GameField): Game =
    this.copy(gameField = newGameField)

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

  def withDelta(value: Float): Game =
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

  def withMovementInterval(newInterval: Float): Game =
    this.copy(movementInterval = newInterval)

  def startNewGame(): Game = {
    Game.initial()
      .withBestScore(bestScore)
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
      gameField = GameField.initial(width, height),
      currentPiece = randomPiece,
      score = 0,
      bestScore = 0,
      movementInterval = 1,
      paused = false,
      gameOver = false,
      gameStarted = false,
      curentDelta = 0
    )
  }
}

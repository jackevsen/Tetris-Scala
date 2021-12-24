package jackevsen.utils

import indigo.shared.BoundaryLocator
import indigo.shared.datatypes.{Point, Radians, Rectangle, Size}
import indigo.shared.dice.Dice
import indigo.shared.scenegraph.{Group, Shape}
import indigo.shared.time.Seconds
import jackevsen.displayObjects._
import jackevsen.model.{Game, GameField, GameFieldPosition}
import jackevsen.renderers.GameRenderer

object GameUtils {
  val cellSize = 32
  val scoreForItem = 10
  
  def normalizeValue(value: Int): Int = {
    (Math.round(value.toDouble / cellSize.toDouble) * cellSize).toInt
  }

  def setPiecePosition(piece:Piece, newPosition: Point, model: Game, boundaryLocator: BoundaryLocator): Piece = {
    val gameFieldSize = getGameFieldSize(model.gameField.width, model.gameField.height)
    val pieceGraphics = GameRenderer.renderPiece(piece)
    val bounds: Rectangle = pieceGraphics.withPosition(newPosition).calculatedBounds(boundaryLocator)

    val movedPiece = piece.withPosition(piece.position.moveTo(newPosition))

    val positionsInGameField = GameUtils.getPieceFragmentsPositions(movedPiece, boundaryLocator)

    if (
      normalizeValue(bounds.x) >= 0 &&
      normalizeValue(bounds.x + bounds.width) <= gameFieldSize.width &&
      normalizeValue(bounds.y + bounds.height) <= gameFieldSize.height &&
      model.gameField.positionsAreFree(positionsInGameField)
    ) {
      movedPiece
    } else {
      piece
    }
  }

  def setPieceRotation(piece: Piece, newRotation: Radians, model: Game, boundaryLocator: BoundaryLocator): Piece = {
    val newPiece = piece.withRotation(newRotation)

    val bounds: Rectangle = GameRenderer.renderPiece(newPiece).calculatedBounds(boundaryLocator)
    val gameFieldSize: Size = getGameFieldSize(model.gameField.width, model.gameField.height)

    val currentPosition = newPiece.position

    val normalizedPieceLeft = normalizeValue(bounds.x)
    val normalizedPieceRight = normalizeValue(bounds.x + bounds.width)

    val newX =
      if (normalizedPieceLeft < 0) {
        currentPosition.withX(
          currentPosition.x + Math.abs(normalizedPieceLeft)
        ).x
      } else if (normalizedPieceRight > gameFieldSize.width) {
        val delta = normalizedPieceRight - gameFieldSize.width
        currentPosition.withX(
          currentPosition.x - delta
        ).x
      } else {
        currentPosition.x
      }

    val normalizedPieceTop = normalizeValue(bounds.y)
    val normalizedPieceBottom = normalizeValue(bounds.y + bounds.height)

    val newY =
      if (normalizedPieceTop < 0) {
        currentPosition.withY(
          currentPosition.y + Math.abs(normalizedPieceTop)
        ).y
      } else if (normalizedPieceBottom > gameFieldSize.height) {
        val delta = normalizedPieceBottom - gameFieldSize.height
        currentPosition.withY(
          currentPosition.y - delta
        ).y
      } else {
        currentPosition.y
      }

    findAndSetFreePositions(
      newPiece.withPosition(Point(newX, newY)),
      model.gameField,
      boundaryLocator
    )
  }

  def getPieceFragmentsPositions(piece: Piece, boundaryLocator: BoundaryLocator): List[GameFieldPosition] = {
    val pieceRenderData = GameRenderer.getPieceRenderData(piece)
    val graphics = GameRenderer.renderPiece(piece)

    val fragments: List[Shape] = graphics
      .children
      .head
      .asInstanceOf[Group]
      .children.asInstanceOf[List[Shape]]

    val pieceBounds = graphics.calculatedBounds(boundaryLocator)

    fragments.map(fragment => {
      val fragmentPosition = fragment.position + (cellSize / 2)
      val coords: Point = (fragmentPosition - pieceRenderData.center).rotateBy(piece.rotation) + pieceRenderData.pivotPoint

      val position: Point = pieceBounds.position + coords
      GameFieldPosition(Math.ceil(position.y.toDouble / cellSize).toInt - 1, Math.ceil(position.x.toDouble / cellSize).toInt - 1)
    })
  }

  def getRandomPiece(dice:Dice): Piece = {
    val piecesList = List(
      PieceT.default,
      PieceSquare.default,
      PieceStick.default,
      PiecePeriscopeLeft.default,
      PiecePeriscopeRight.default,
      PieceDogLeft.default,
      PieceDogRight.default)

    piecesList(dice.roll(piecesList.length) - 1)
  }

  def getDice(): Dice =
    Dice.diceSidesN(7, 1)

  def findAndSetFreePositions(piece: Piece, gameField: GameField, boundaryLocator: BoundaryLocator): Piece = {
    val positionOnGameField = getPieceFragmentsPositions(piece, boundaryLocator)
    if (!gameField.positionsAreFree(positionOnGameField)) {
      val curPosition = piece.position
      findAndSetFreePositions(
        piece.withPosition(curPosition.withY(curPosition.y - cellSize)),
        gameField,
        boundaryLocator
      )
    } else {
      piece
    }
  }

  def normalizeRotation(rotation: Radians): Radians = {
    if (rotation == Radians.TAU) {
      Radians.zero
    } else {
      rotation
    }
  }

  def getGameFieldSize(width: Int, height: Int): Size =
    Size(width * cellSize, height * cellSize)

  def updateGameModelByTimeout(model: Game, delta: Seconds, boundaryLocator: BoundaryLocator, dice: Dice): Game = {
    if(model.curentDelta + delta >= model.movementInterval) {
      val newPosition = model.currentPiece.position.withY(
        model.currentPiece.position.y + cellSize
      )

      updateGameModel(model, newPosition, boundaryLocator, dice)
        .withDelta(Seconds(0))
    } else {
      model
        .withDelta(model.curentDelta + delta)
    }
  }

  def updateGameModel(model: Game, newPosition: Point, boundaryLocator: BoundaryLocator, dice: Dice): Game = {
    val prevPosition = model.currentPiece.position

    val newPiece = setPiecePosition(
      model.currentPiece,
      newPosition,
      model,
      boundaryLocator
    )

    val positionsInGameField = getPieceFragmentsPositions(newPiece, boundaryLocator)

    val maxRow  = positionsInGameField.foldLeft(0)((acc, elem) => {
      if (elem.row > acc) {
        elem.row
      } else {
        acc
      }
    })

    if (newPosition.y != prevPosition.y && newPiece.position.y != newPosition.y) {
      processNextPieceUpdate(
        model.withGameField(
          model.gameField.withTakenPositions(
            getPieceFragmentsPositions(model.currentPiece, boundaryLocator)
          )
        ),
        dice,
        boundaryLocator
      )
    } else if (maxRow == model.gameField.height - 1) {
      processNextPieceUpdate(
        model.withGameField(
          model.gameField.withTakenPositions(positionsInGameField)
        ),
        dice,
        boundaryLocator
      )
    } else {
      model.withCurrentPiece(newPiece)
    }
  }

  def processNextPieceUpdate(model: Game, dice: Dice, boundaryLocator: BoundaryLocator): Game = {
    val (removedRowsCount, newGameField) = model.gameField.removeFilledRows()
    val scoreForRows = removedRowsCount * (scoreForItem * model.gameField.width)

    val newModel = setRandomPiece(model, dice)
      .withScore(model.score + scoreForRows)
      .withGameField(newGameField)

    if(
      !model.gameField.positionsAreFree(
        getPieceFragmentsPositions(
          newModel.currentPiece, boundaryLocator
        )
      )
    ) {
      if (model.score > model.bestScore) {
        newModel
          .withGameOver(true)
          .withBestScore(model.score)
      } else {
        newModel
          .withGameOver(true)
      }
    } else {
      newModel
    }
  }

  def setRandomPiece(model: Game, dice: Dice): Game = {
    val nextPiece = getRandomPiece(dice)
      .withPosition(Point((model.gameField.width / 2) * cellSize, 0))

    model
      .withCurrentPiece(nextPiece)
  }
}

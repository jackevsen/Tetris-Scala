package jackevsen.utils

import jackevsen.displayObjects.{
  GameField,
  Piece,
  PieceDogLeft,
  PieceDogRight,
  PiecePeriscopeLeft,
  PiecePeriscopeRight,
  PieceSquare,
  PieceStick,
  PieceT
}
import indigo.shared.BoundaryLocator
import indigo.shared.datatypes.{Point, Radians, Rectangle, Size}
import indigo.shared.dice.Dice
import indigo.shared.scenegraph.{Group, Shape}
import jackevsen.model.{GameFieldModel, GameFieldPosition}
import jackevsen.renderers.GameRenderer

object GameUtils {
  val cellSize = 32
  val scoreForItem = 10
  
  def normalizeValue(value: Int): Int = {
    (Math.round(value.toDouble / cellSize.toDouble) * cellSize).toInt
  }

  def setPiecePosition(piece:Piece, newPosition: Point, gameFieldModel: GameFieldModel, boundaryLocator: BoundaryLocator): Piece = {
    val gameFieldSize = getGameFieldSize(gameFieldModel.gameField)
    val pieceGraphics = GameRenderer.renderPiece(piece)
    val bounds: Rectangle = pieceGraphics.withPosition(newPosition).calculatedBounds(boundaryLocator)

    val movedPiece = piece.withPosition(piece.position.moveTo(newPosition))

    val positionsInGameField = GameUtils.getPieceFragmentsPositions(movedPiece, boundaryLocator)

    if (
      normalizeValue(bounds.x) >= 0 &&
      normalizeValue(bounds.x + bounds.width) <= gameFieldSize.width &&
      normalizeValue(bounds.y + bounds.height) <= gameFieldSize.height &&
      gameFieldModel.positionsAreFree(positionsInGameField)
    ) {
      movedPiece
    } else {
      piece
    }
  }

  def setPieceRotation(piece: Piece, newRotation: Radians, gameFieldModel: GameFieldModel, boundaryLocator: BoundaryLocator): Piece = {
    val gameField = gameFieldModel.gameField

    val newPiece = piece.withRotation(newRotation)

    val bounds: Rectangle = GameRenderer.renderPiece(newPiece).calculatedBounds(boundaryLocator)
    val gameFieldSize: Size = getGameFieldSize(gameField)

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

    setFreePositionsOnGameField(
      newPiece.withPosition(Point(newX, newY)),
      gameFieldModel,
      boundaryLocator
    )
  }

  def getPieceSize(piece: Piece): Size = {
    val pieceRenderData = GameRenderer.getPieceRenderData(piece)

    if (pieceRenderData.shapeCoords.length == 0) {
      return Size(0, 0)
    }

    val maxPoint: Point = pieceRenderData.shapeCoords.foldLeft(Point(0, 0))((acc, curPiece) => {
      val newX =
        if (curPiece.x > acc.x) {
          curPiece.x
        } else {
          acc.x
        }

      val newY =
        if (curPiece.y > acc.y) {
          curPiece.y
        } else {
          acc.y
        }

      acc
        .withX(newX)
        .withY(newY)
    })

    val size: Size = Size((maxPoint.x + 1) * cellSize, (maxPoint.y + 1) * cellSize)

    if (piece.rotation == Radians.PIby2 || piece.rotation == Radians.PIby2 * 2) {
      size.invert
    } else {
      size
    }
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
    val randomPiece = piecesList(dice.roll(piecesList.length) - 1)

    randomPiece
  }

  def getDice(): Dice =
    Dice.diceSidesN(7, 1)

  def setFreePositionsOnGameField(piece: Piece, gameFieldModel: GameFieldModel, boundaryLocator: BoundaryLocator): Piece = {
    val positionOnGameField = getPieceFragmentsPositions(piece, boundaryLocator)
    if (!gameFieldModel.positionsAreFree(positionOnGameField)) {
      val curPosition = piece.position
      setFreePositionsOnGameField(
        piece.withPosition(curPosition.withY(curPosition.y - cellSize)),
        gameFieldModel,
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

  def getGameFieldSize(gameField: GameField): Size =
    Size(gameField.width * cellSize, gameField.height * cellSize)
}

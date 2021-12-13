package utils

import displayObjects.GameField
import displayObjects.pieces._
import indigo.shared.BoundaryLocator
import indigo.shared.datatypes.{Point, Radians, Rectangle, Size}
import indigo.shared.dice.Dice
import indigo.shared.scenegraph.Group
import model.GameFieldPosition

object GameUtils {
  val cellSize = 32
  val scoreForItem = 10
  
  def normalizeValue(value: Int, cellSize: Int): Int = {
    (Math.round(value.toDouble / cellSize.toDouble) * cellSize).toInt
  }

  def setPiecePosition(piece:Piece, newPosition: Point, gameField: GameField, boundaryLocator: BoundaryLocator): Unit = {
    val gameFieldSize = gameField.getSize()
    val pieceGraphics = piece.draw()
    val bounds: Rectangle = pieceGraphics.withPosition(newPosition).calculatedBounds(boundaryLocator)

    if (
      normalizeValue(bounds.x, piece.cellSize) >= 0 &&
      normalizeValue(bounds.x + bounds.width, piece.cellSize) <= gameFieldSize.width &&
      normalizeValue(bounds.y + bounds.height, piece.cellSize) <= gameFieldSize.height
    ) {
      piece.setPosition(piece.getPosition.moveTo(newPosition))
    }
  }

  def setPieceRotation(piece: Piece, newRotation: Radians, gameField: GameField, boundaryLocator: BoundaryLocator): Unit = {
    piece.setRotation(newRotation)
    val bounds: Rectangle = piece.draw().calculatedBounds(boundaryLocator)
    val gameFieldSize: Size = gameField.getSize()

    var currentPosition = piece.getPosition

    val normalizedPieceLeft = normalizeValue(bounds.x, piece.cellSize)
    val normalizedPieceRight = normalizeValue(bounds.x + bounds.width, piece.cellSize)

    if (normalizedPieceLeft < 0) {
      currentPosition = currentPosition.withX(
        currentPosition.x + Math.abs(normalizedPieceLeft)
      )
    } else if (normalizedPieceRight > gameFieldSize.width) {
      val delta = normalizedPieceRight - gameFieldSize.width
      currentPosition = currentPosition.withX(
        currentPosition.x - delta
      )
    }

    val normalizedPieceTop = normalizeValue(bounds.y, piece.cellSize)
    val normalizedPieceBottom = normalizeValue(bounds.y + bounds.height, piece.cellSize)

    if (normalizedPieceTop < 0) {
      currentPosition = currentPosition.withY(
        currentPosition.y + Math.abs(normalizedPieceTop)
      )
    } else if (normalizedPieceBottom > gameFieldSize.height) {
      val delta = normalizedPieceBottom - gameFieldSize.height
      currentPosition = currentPosition.withY(
        currentPosition.y - delta
      )
    }

    piece.setPosition(currentPosition)
  }

  def getPieceSize(piece: Piece): Size = {
    if (piece.pathCoords == Nil || piece.pathCoords.length == 0) {
      return Size(0, 0)
    }

    val maxPoint: Point = piece.pathCoords.foldLeft(Point(0, 0))((acc, curPiece) => {
      var curAcc = acc
      if (curPiece.x > acc.x) {
        curAcc = acc.withX(curPiece.x)
      }

      if (curPiece.y > acc.y) {
        curAcc = acc.withY(curPiece.y)
      }

      curAcc
    })

    Size((maxPoint.x + 1) * piece.cellSize, (maxPoint.y + 1) * piece.cellSize)
  }

  def getPieceFragmentsPositions(piece: Piece, boundaryLocator: BoundaryLocator): List[GameFieldPosition] = {
    val graphics = piece.draw()
    val fragments = graphics
      .children
      .head
      .asInstanceOf[Group]
      .children
    val pieceBounds = graphics.calculatedBounds(boundaryLocator)

    fragments.map(fragment => {
      val fragmentPosition = fragment.position + (piece.cellSize / 2)
      val coords: Point = (fragmentPosition - piece.center).rotateBy(piece.getRotation) + piece.getCenterPointWithRotation()

      val position: Point = pieceBounds.position + coords
      GameFieldPosition(Math.ceil(position.y.toDouble / piece.cellSize).toInt - 1, Math.ceil(position.x.toDouble / piece.cellSize).toInt - 1)
    })

  }

  def getRandomPiece(dice:Dice): Piece = {
    val piecesList = List(PieceT(), PieceSquare(), PieceStick(), PiecePeriscopeLeft(), PiecePeriscopeRight(), PieceDogLeft(), PieceDogRight())
    val randomPiece = piecesList(dice.roll(piecesList.length) - 1)

    randomPiece
  }
}

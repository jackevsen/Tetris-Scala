package displayObjects.pieces

import displayObjects.common.GameObject
import indigo.shared.datatypes.{Point, Size, Radians}
import indigo.shared.scenegraph.Group
import utils.{GameUtils, GraphicsUtils}

trait Piece extends GameObject{
  val pathCoords: List[Point]
  val center: Point

  def getCenterPointWithRotation(): Point

  def getSize(): Size = {
    val size = GameUtils.getPieceSize(this)
    if (rotation == Radians.PIby2 || rotation == Radians.PIby2 * 2) {
      size.invert
    } else {
      size
    }
  }

  def draw(): Group = {
    val pieceGraphics = GraphicsUtils.drawGamePiece(this, cellSize)
      .withRef(center)
      .moveTo(center)
      .rotateTo(rotation)

    Group(pieceGraphics)
      .moveTo(position)
  }
}

final case class PieceNone() extends Piece {
  val pathCoords: List[Point] = List()
  val center: Point = Point(0, 0)

  def getCenterPointWithRotation(): Point =
    Point(0, 0)
}

final case class PieceT() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 0),
    Point(0, 1),
    Point(0, 2),
    Point(1, 1)
  )

  val center: Point = Point(cellSize / 2, (cellSize * 1.5).toInt)

  def getCenterPointWithRotation(): Point =
    if (rotation == Radians.zero) {
      center
    } else if (rotation == Radians.PIby2) {
      Point((cellSize * 1.5).toInt, cellSize / 2)
    } else {
      Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)
    }
}

final case class PieceSquare() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 0),
    Point(0, 1),
    Point(1, 0),
    Point(1, 1)
  )

  val center: Point = Point(cellSize, cellSize)

  def getCenterPointWithRotation(): Point =
    center
}

final case class PieceStick() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 0),
    Point(0, 1),
    Point(0, 2),
    Point(0, 3)
  )

  val center: Point = Point(cellSize, cellSize * 2)

  def getCenterPointWithRotation(): Point =
    if (rotation == Radians.zero) {
      center
    } else if(rotation == Radians.PIby2) {
      Point(cellSize * 2, cellSize)
    } else if (rotation == Radians.PI) {
      Point(0, cellSize * 2)
    } else {
      Point(cellSize * 2, 0)
    }
}

final case class PiecePeriscopeLeft() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 2),
    Point(1, 0),
    Point(1, 1),
    Point(1, 2)
  )

  val center: Point = Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)

  def getCenterPointWithRotation(): Point =
    if (rotation == Radians.zero || rotation == Radians.PIby2) {
      center
    } else if (rotation == (Radians.PI)) {
      Point(cellSize / 2, (cellSize * 1.5).toInt)
    } else {
      Point((cellSize * 1.5).toInt, cellSize / 2)
    }
}

final case class PiecePeriscopeRight() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 0),
    Point(0, 1),
    Point(0, 2),
    Point(1, 2)
  )

  val center: Point = Point(cellSize / 2, (cellSize * 1.5).toInt)

  def getCenterPointWithRotation(): Point =
    if (rotation == Radians.zero) {
      center
    } else if (rotation == Radians.PIby2) {
      Point((cellSize * 1.5).toInt, cellSize / 2)
    } else {
      Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)
    }
}

final case class PieceDogLeft() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 1),
    Point(0, 2),
    Point(1, 0),
    Point(1, 1)
  )

  val center: Point = Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt )

  def getCenterPointWithRotation(): Point =
    if (rotation == Radians.zero || rotation == Radians.PIby2) {
      center
    } else if (rotation == (Radians.PI)) {
      Point(cellSize / 2, (cellSize * 1.5).toInt)
    } else {
      Point((cellSize * 1.5).toInt, cellSize / 2)
    }
}

final case class PieceDogRight() extends Piece {
  val pathCoords: List[Point] = List(
    Point(0, 0),
    Point(0, 1),
    Point(1, 1),
    Point(1, 2)
  )

  val center: Point = Point(cellSize / 2, (cellSize * 1.5).toInt)

  def getCenterPointWithRotation(): Point =
    if (rotation == Radians.zero) {
      center
    } else if (rotation == Radians.PIby2) {
      Point((cellSize * 1.5).toInt, cellSize / 2)
    } else {
      Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)
    }
}

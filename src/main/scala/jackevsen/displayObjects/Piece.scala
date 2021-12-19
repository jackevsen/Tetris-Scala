package jackevsen.displayObjects

import indigo.shared.datatypes.{Point, Radians}
import jackevsen.utils.GameUtils

sealed trait Piece extends GameObject[Piece] {
  val rotatable: Boolean
  val rotation: Radians

  def withRotation(newRotation: Radians): Piece
}

final case class PieceT(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PieceT =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PieceT =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PieceT {
  def default = PieceT(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = true
  )
}

final case class PieceSquare(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PieceSquare =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PieceSquare =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PieceSquare {
  def default = PieceSquare(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = false
  )
}

final case class PieceStick(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PieceStick =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PieceStick =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PieceStick {
  def default = PieceStick(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = true
  )
}

final case class PiecePeriscopeLeft(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PiecePeriscopeLeft =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PiecePeriscopeLeft =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PiecePeriscopeLeft {
  def default = PiecePeriscopeLeft(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = true
  )
}

final case class PiecePeriscopeRight(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PiecePeriscopeRight =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PiecePeriscopeRight =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PiecePeriscopeRight {
  def default = PiecePeriscopeRight(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = true
  )
}

final case class PieceDogLeft(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PieceDogLeft =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PieceDogLeft =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PieceDogLeft {
  def default = PieceDogLeft(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = true
  )
}

final case class PieceDogRight(
  position: Point,
  rotation: Radians,
  rotatable: Boolean
) extends Piece {
  def withPosition(newPosition: Point): PieceDogRight =
    this.copy(position = newPosition)

  def withRotation(newRotation: Radians): PieceDogRight =
    this.copy(rotation = GameUtils.normalizeRotation(newRotation))
}

object PieceDogRight {
  def default = PieceDogRight(
    position = Point(0, 0),
    rotation = Radians.zero,
    rotatable = true
  )
}

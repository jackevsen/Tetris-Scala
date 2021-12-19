package jackevsen.displayObjects

import indigo.shared.datatypes.Point

case class GameField(width: Int, height: Int, position: Point) extends GameObject[GameField] {

  def withPosition(newPosition: Point): GameField =
    this.copy(position = newPosition)
}

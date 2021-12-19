package jackevsen.displayObjects

import indigo.shared.datatypes.Point

trait GameObject[A <: GameObject[A]] {
  val position: Point

  def withPosition(newPosition: Point): A
}

package displayObjects.common

import indigo.shared.scenegraph.Group
import indigo.shared.datatypes.{Point, Radians}
import utils.GameUtils

trait GameObject {
  val cellSize: Int = GameUtils.cellSize

  protected var position: Point = Point(0, 0)
  protected var rotation: Radians = Radians(0)

  def getPosition: Point =
    position

  def setPosition(newPosition: Point): Unit =
    position = newPosition

  def getRotation: Radians =
    rotation

  def setRotation(newRotation: Radians): Unit = {
    if (newRotation == Radians.TAU) {
      rotation = Radians.zero
    } else {
      rotation = newRotation
    }
  }

  def draw(): Group
}

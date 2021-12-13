package displayObjects

import displayObjects.common.GameObject
import indigo.shared.datatypes.Size
import indigo.shared.scenegraph.Group
import utils.GraphicsUtils

case class GameField(width: Int, height: Int) extends GameObject {
  def draw(): Group =
    GraphicsUtils.drawGameField(width, height, cellSize)

  def getSize(): Size =
    Size(width * cellSize, height * cellSize)
}

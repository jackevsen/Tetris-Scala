package jackevsen.renderers

import indigo.shared.datatypes.{Point, Radians}
import indigo.shared.scenegraph.Group
import jackevsen.displayObjects._
import jackevsen.utils.GameUtils.cellSize
import jackevsen.utils.GraphicsUtils

case class RenderData(center: Point, shapeCoords: List[Point], cellSize: Int, pivotPoint: Point)

object GameRenderer {
  def renderGameField(gameField: GameField): Group =
    GraphicsUtils.drawGameField(gameField.width, gameField.height, cellSize)

  def renderPiece(piece: Piece): Group = {
    val pieceRenderData = getPieceRenderData(piece)

    val pieceGraphics = GraphicsUtils.drawGamePiece(pieceRenderData)
      .withRef(pieceRenderData.center)
      .moveTo(pieceRenderData.center)
      .rotateTo(piece.rotation)

    Group(pieceGraphics)
      .moveTo(piece.position)
  }

  def getPieceRenderData(piece: Piece): RenderData = piece match {
    case p: PieceT  =>
      val center = Point(cellSize / 2, (cellSize * 1.5).toInt)

      val pivotPoint: Point =
        if (p.rotation == Radians.zero)
          center
        else if (p.rotation == Radians.PIby2)
          Point((cellSize * 1.5).toInt, cellSize / 2)
        else
          Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)

      RenderData(
        center,
        List(
          Point(0, 0),
          Point(0, 1),
          Point(0, 2),
          Point(1, 1)
        ),
        cellSize,
        pivotPoint
      )
    case p: PieceSquare  =>
      val center = Point(cellSize, cellSize)

      RenderData(
        center,
        List(
          Point(0, 0),
          Point(0, 1),
          Point(1, 0),
          Point(1, 1)
        ),
        cellSize,
        center
      )
    case p: PieceStick  =>
      val center = Point(cellSize, cellSize * 2)

      val pivotPoint: Point =
        if (p.rotation == Radians.zero)
          center
        else if(p.rotation == Radians.PIby2)
          Point(cellSize * 2, cellSize)
        else if (p.rotation == Radians.PI)
          Point(0, cellSize * 2)
        else
          Point(cellSize * 2, 0)

      RenderData(
        center,
        List(
          Point(0, 0),
          Point(0, 1),
          Point(0, 2),
          Point(0, 3)
        ),
        cellSize,
        pivotPoint
      )
    case p: PiecePeriscopeLeft  =>
      val center = Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)

      val pivotPoint: Point =
        if (p.rotation == Radians.zero || p.rotation == Radians.PIby2) {
          center
        } else if (p.rotation == (Radians.PI)) {
          Point(cellSize / 2, (cellSize * 1.5).toInt)
        } else {
          Point((cellSize * 1.5).toInt, cellSize / 2)
        }

      RenderData(
        center,
        List(
          Point(0, 2),
          Point(1, 0),
          Point(1, 1),
          Point(1, 2)
        ),
        cellSize,
        pivotPoint
      )
    case p: PiecePeriscopeRight  =>
      val center = Point(cellSize / 2, (cellSize * 1.5).toInt)

      val pivotPoint: Point =
        if (p.rotation == Radians.zero)
          center
        else if (p.rotation == Radians.PIby2)
          Point((cellSize * 1.5).toInt, cellSize / 2)
        else
          Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)

      RenderData(
        center,
        List(
          Point(0, 0),
          Point(0, 1),
          Point(0, 2),
          Point(1, 2)
        ),
        cellSize,
        pivotPoint
      )
    case p: PieceDogLeft  =>
      val center = Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)

      val pivotPoint: Point =
        if (p.rotation == Radians.zero || p.rotation == Radians.PIby2) {
          center
        } else if (p.rotation == Radians.PI) {
          Point(cellSize / 2, (cellSize * 1.5).toInt)
        } else {
          Point((cellSize * 1.5).toInt, cellSize / 2)
        }

      RenderData(
        center,
        List(
          Point(0, 1),
          Point(0, 2),
          Point(1, 0),
          Point(1, 1)
        ),
        cellSize,
        pivotPoint
      )
    case p: PieceDogRight  =>
      val center = Point(cellSize / 2, (cellSize * 1.5).toInt)

      val pivotPoint: Point =
        if (p.rotation == Radians.zero)
          center
        else if (p.rotation == Radians.PIby2)
          Point((cellSize * 1.5).toInt, cellSize / 2)
        else
          Point((cellSize * 1.5).toInt, (cellSize * 1.5).toInt)

      RenderData(
        Point(cellSize / 2, (cellSize * 1.5).toInt),
        List(
          Point(0, 0),
          Point(0, 1),
          Point(1, 1),
          Point(1, 2)
        ),
        cellSize,
        pivotPoint
      )
  }
}

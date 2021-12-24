package jackevsen.renderers

import indigo.shared.config.GameViewport
import indigo.shared.datatypes._
import indigo.shared.scenegraph.{Group, SceneNode, Shape, TextBox}
import jackevsen.displayObjects._
import jackevsen.fonts.FontBoxy
import jackevsen.model
import jackevsen.model.{Game, GameFieldPosition, RenderData}
import jackevsen.utils.GameUtils
import jackevsen.utils.GameUtils.cellSize

object GameRenderer {
  def renderGameField(width: Int, height: Int): Group = {
    val countVert = width + 1
    val countHorizont = height + 1

    val listVert: List[Shape.Line] =
      (0 until countVert).map(i => {
        Shape.Line(Point(i * cellSize, 0), Point(i * cellSize, height * cellSize), Stroke(3, RGBA.White))
      }).toList

    val listHorizont: List[Shape.Line] =
      (0 until countHorizont).map(i => {
        Shape.Line(Point(0, i * cellSize), Point(width * cellSize, i * cellSize), Stroke(3, RGBA.White))
      }).toList

    Group(listVert ::: listHorizont)
  }

  def renderPiece(piece: Piece): Group = {
    val pieceRenderData = getPieceRenderData(piece)

    val cellSize = pieceRenderData.cellSize
    val shapes: List[Shape] = pieceRenderData.shapeCoords.map(p => {
      Shape.Box(
        Rectangle(p * cellSize, Size(cellSize, cellSize)),
        Fill.Color(RGBA.White),
        Stroke(1, RGBA.Black)
      )
    })

    val pieceGraphics = Group(shapes)
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

      model.RenderData(
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

      model.RenderData(
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

      model.RenderData(
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

      model.RenderData(
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

      model.RenderData(
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

      model.RenderData(
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

  def renderText(text: String, width: Int, height: Int, fontSize: Int = 20): TextBox = {
    TextBox(
      text,
      width,
      height
    )
      .withFontFamily(FontBoxy.fontFamily)
      .withColor(RGBA.White)
      .withFontSize(Pixels(fontSize))
  }

  def renderTakenPositions(positions: List[GameFieldPosition], cellSize: Int): Group = {
    Group(
      positions.map(p => {
        val point = Point(p.column, p.row)
        Shape.Box(
          Rectangle(point * cellSize, Size(cellSize, cellSize)),
          Fill.Color(RGBA.White),
          Stroke(1, RGBA.Black)
        )
      })
    )
  }

  def renderScene(model: Game, viewport: GameViewport): List[SceneNode] = {
    if (!model.gameStarted && !model.gameOver) {
      List(
        Group(
          renderText("TETRIS", viewport.width, 50, 50).moveTo(0, 30).alignCenter,
          renderText("Control:", viewport.width, 30, 30).moveTo(0, 120).alignCenter,
          Group(
            renderText("Arrow Right - move a figure right", 300, 25),
            renderText("Arrow Left - move a figure left", 300, 25).moveTo(0, 30),
            renderText("Arrow Down - speed up a figure", 300, 25).moveTo(0, 60),
            renderText("Arrow Up - rotate a figure", 300, 25).moveTo(0, 90),
            renderText("P - pause the game", 300, 25).moveTo(0, 120)
          ).moveTo(viewport.center.x / 2, 160),
          renderText("Press 'Space bar' to start...", viewport.width, 35, 25).moveTo(0, 420).alignCenter,
        )
      )
    } else if (model.gameStarted) {
      val gameFieldSize = GameUtils.getGameFieldSize(model.gameField.width, model.gameField.height)

      val list = List(
        GameRenderer.renderGameField(model.gameField.width, model.gameField.height).moveTo(Point(viewport.center.x - gameFieldSize.width / 2, 50))
          .addChild(renderTakenPositions(model.gameField.getTakenPositions(), GameUtils.cellSize))
          .addChild(GameRenderer.renderPiece(model.currentPiece)),
        renderText(s"score: ${model.score}", viewport.width, 20).moveTo(0, 10).alignCenter,
      )

      if (model.paused) {
        list ::: List(renderText("PAUSE", viewport.width, 50, 50).moveTo(0, viewport.center.y - 100).alignCenter)
      } else {
        list
      }
    } else if (model.gameOver) {
      List(
        renderText("Game Over", viewport.width, 45, 40).moveTo(0, 100).alignCenter,
        renderText(s"Your score: ${model.score}", viewport.width, 30, 25).moveTo(0, 170).alignCenter,
        renderText(s"Your best score: ${model.bestScore}", viewport.width, 30, 25).moveTo(0, 210).alignCenter,
        renderText("Press 'Space bar' to continue...", viewport.width, 35, 25).moveTo(0, 420).alignCenter,
      )
    } else {
      List()
    }
  }
}
